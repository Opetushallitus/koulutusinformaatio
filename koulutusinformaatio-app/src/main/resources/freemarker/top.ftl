<header id="siteheader">
    <div class="logo-bg">
        <div class="container">
            <a id="home-link" href="/">
                <img src="/app/img/opintopolku_large-${lang}.png"  alt="<@msg "sitename"/>"/>
            </a>
            <span data-ki-banner></span>
            <div class="actions">
                <ul >
                    <li><a href="/fi/hakemisto/oppilaitokset/${letter}" onclick="setCookie('i18next', 'fi', 999)">Suomeksi</a></li>
                    <li><a href="/sv/hakemisto/oppilaitokset/${letter}" onclick="setCookie('i18next', 'sv', 999)">På svenska</a></li>
                </ul>
            </div>
            <div class="actions primarylinks">
                <ul>
                    <li class="icon basket">
                        <a id="appbasket-link" href="${baseUrl}muistilista">
                            <span><@msg "applicationbasket"/></span> <span></span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</header>