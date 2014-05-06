<header id="siteheader">
    <div class="logo-bg">
        <div class="container">
            <a id="home-link" href="/">
                <img src="${baseUrl}app/img/opintopolku_large-${lang}.png"  alt="<@msg "sitename"/>"/>
            </a>
            <span data-ki-banner></span>
            <div class="actions">
                <ul >
                    <li><a href="${baseUrl}fi/hakemisto/oppilaitokset/${letter}" onclick="setCookie('i18next', 'fi', 999)">Suomeksi</a></li>
                    <li><a href="${baseUrl}sv/hakemisto/oppilaitokset/${letter}" onclick="setCookie('i18next', 'sv', 999)">På svenska</a></li>
                </ul>
            </div>
            <div class="actions primarylinks">
                <ul>
                    <li class="icon basket">
                        <a id="appbasket-link" href="${ngBaseUrl}muistilista">
                            <span><@msg "applicationbasket"/></span> <span></span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</header>