#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
reload(sys)
sys.setdefaultencoding('utf8')

import inspect
import os
import re
from jinja2 import Environment, FileSystemLoader
from datetime import datetime
from data import get

RESULTS_PER_PAGE = 20

def assignment_variable(fn):
    m = re.search(r"^\d+_(.*)=$", fn)
    if m:
        return m.group(1)
    return None

def is_hidden_file(fn):
    return fn.endswith("-")

def is_index_file(fn):
    return fn.endswith("+")

def is_assignment_file(fn):
    return fn.endswith("=")

def eval_python(src_fn, data):
    locals = data.copy()
    locals["retval"] = None
    execfile(src_fn, {}, locals)
    return locals["retval"]

def _(item, dn_key):
    for k in dn_key.split("."):
        if k == "" or not isinstance(item, dict) or k not in item:
            return None
        item = item[k]
    return item

def dateformat(value, format="%d.%m.%Y"):
    return datetime.fromtimestamp(value/1000).strftime(format)

def datetimeformat(value, format="%d.%m.%Y %H:%M"):
    return datetime.fromtimestamp(value/1000).strftime(format)

def datetimeinterval(startts, endts):
    start = datetime.fromtimestamp(startts/1000)
    end = datetime.fromtimestamp(endts/1000)
    if start.year == end.year and start.month == end.month and start.day == end.day:
        return start.strftime("%d.%m.%Y %H:%M") + " - " + end.strftime("%H:%M")
    else:
        return start.strftime("%d.%m.%Y %H:%M") + " - " + end.strftime("%d.%m.%Y %H:%M")

def separated_list(values, sep=", "):
    return sep.join(values)

def index_links(links, title="ctx", chars=3, page=None, sep=" "):
    a = []
    for (idx, lnk) in enumerate(links):
        first = _(lnk["first"], title)[0:chars]
        last = _(lnk["last"], title)[0:chars]
        url = lnk["url"]
        if idx + 1 == page:
            a.append(s("    <li>{first}-{last}</li>\n"))
        else:
            a.append(s("    <li><a href='{url}'>{first}-{last}</a></li>\n"))
    return sep.join(a)

def get_templ(src_fn):
    env = Environment(loader=FileSystemLoader([os.path.dirname(src_fn), "../src/lib-"]), lstrip_blocks=True, trim_blocks=True)
    env.filters['dateformat'] = dateformat
    env.filters['datetimeformat'] = datetimeformat
    env.filters['datetimeinterval'] = datetimeinterval
    env.filters['separated_list'] = separated_list
    env.filters['index_links'] = index_links
    return env.get_template(os.path.basename(src_fn))

def render_templ_file(src_fn, out_fn, data, ctx, page=None, maxpage=None, index_links=None, prev_page=None, next_page=None):
    data["ctx"] = ctx
    data["page"] = page
    data["maxpage"] = maxpage
    data["index_links"] = index_links
    data["results_per_page"] = RESULTS_PER_PAGE
    if prev_page:
        data["prev_page"] = prev_page
    else:
        data.pop("prev_page", None)
    if next_page:
        data["next_page"] = next_page
    else:
        data.pop("next_page", None)
    templ = get_templ(src_fn)
    with open(out_fn, "w") as f:
        f.write(templ.render(**data))

# this just a shortcut for the python format, references to local variables are automatically expanded
def s(template, **kwargs):
    "Usage: s(string, **locals())"
    if not kwargs:
        frame = inspect.currentframe()
        try:
            kwargs = frame.f_back.f_locals
        finally:
            del frame
        if not kwargs:
            kwargs = globals()

    return template.format(**kwargs)

def make_out_fn(fn):
    "remove the operator signs and sort order"
    fn = re.sub(r"^\d+_", "", fn)
    fn = re.sub(r"[=+]$", "", fn)
    return fn

def mkdir_p(dir):
    if not dir.endswith("/."):
        try:
            os.mkdir(dir)
        except:
            pass

def expand_fn(fn, ctx):
    "Expand file name (possibly with variable name) to each member in a list"
    if isinstance(ctx, list):
        return dict([(fn.format(**x), x) for x in ctx])
    elif isinstance(ctx, dict):
        p = fn.format(**ctx)
        return { p: ctx }
    else:
        return { fn: ctx }

def workdir_ctx(data, workdir):
    for s in workdir.split("/"):
        if s == '.':
            continue
        data = data[s]
    return data

def locales(lngs):
    result = {}
    for lng in lngs:
        locale = get(s("/app/locales/language-{lng}.json"))
        plain = get(s("/app/locales/plain-{lng}.json"))
        locale.update(plain)
        result[s("locales{lng}")] = locale
    return result

def eval_dir(src_workdir=".", out_workdir=".", data=None):
    "Evaluate a directory as operations"

    mkdir_p(out_workdir)

    # sort 
    for unexpanded_fn in sorted(os.listdir(src_workdir)):

        src_path = s("{src_workdir}/{unexpanded_fn}")

        # data may have been changed, so we need to recalculate evaluation context each time
        ctx = workdir_ctx(data, out_workdir)

        # skip files ending with dash -, such as base templates
        if is_hidden_file(unexpanded_fn):

            continue
        
        # for index template files (ending with +) pass the context as the list, split by RESULTS_PER_PAGE
        elif is_index_file(unexpanded_fn) and out_workdir != '.':

            maxpage = int(len(ctx)/RESULTS_PER_PAGE)

            if len(ctx) % RESULTS_PER_PAGE > 0:
                maxpage += 1

            # create index links
            index_links = []
            for page in list(xrange(1, maxpage + 1)):

                fn = expand_fn(unexpanded_fn, [{ "page": page }]).keys()[0]
                out_fn = make_out_fn(fn)

                first = (page - 1) * RESULTS_PER_PAGE
                if page == maxpage:
                    last = len(ctx) - 1
                else:
                    last = page * RESULTS_PER_PAGE

                #print first
                #print last
                #print page
                #print out_fn
                #print ctx[first]
                #print ctx[last]
                index_links.append({ "page": page, "url": out_fn, "first": ctx[first], "last": ctx[last] })

            # render pages
            for page in list(xrange(1, maxpage + 1)) + ["all"]:

                fn = expand_fn(unexpanded_fn, [{ "page": page }]).keys()[0]
                out_fn = make_out_fn(fn)
                out_path = s("{out_workdir}/{out_fn}")

                prev_page = None
                next_page = None

                if page == "all":
                    items = ctx
                else:
                    if page > 1:
                        prev_page = page - 1
                    if page < maxpage:
                        next_page = page + 1
                    items = ctx[(page - 1) * RESULTS_PER_PAGE:page * RESULTS_PER_PAGE]

                render_templ_file(src_path, out_path, data, items, page=page, maxpage=maxpage, index_links=index_links, prev_page=prev_page, next_page=next_page)

        # expand file name into a list of output file names (variable substitution), and context to each
        else:

            for (fn, fn_ctx) in expand_fn(unexpanded_fn, ctx).iteritems():
    
                out_fn = make_out_fn(fn)
                out_path = s("{out_workdir}/{out_fn}")
    
                if os.path.isdir(src_path):
    
                    # directory is an iterator, loop through the context (which in this case must be a list)
                    eval_dir(src_path, out_path, data)
    
                else:
    
                    # assingment operator is when the file ends with "=", those we evaluate as python script, 
                    # take the value of the 'retval' variable set by the script, and set the variable based 
                    # on the file name
                    if is_assignment_file(fn):

                        data[assignment_variable(fn)] = eval_python(src_path, data)
    
                    # for regular template files, iterate through each context in the context list
                    else: 

                        render_templ_file(src_path, out_path, data, fn_ctx)
    
data = {}
lngs = ["fi", "sv"]
locales = locales(lngs)
data["locales"] = locales
os.chdir("out")
eval_dir(src_workdir="../src", out_workdir=".", data=data)
