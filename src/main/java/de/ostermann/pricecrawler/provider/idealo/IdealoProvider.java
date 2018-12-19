package de.ostermann.pricecrawler.provider.idealo;

import de.ostermann.pricecrawler.BrowserConstants;
import de.ostermann.pricecrawler.provider.PriceProvider;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class IdealoProvider implements PriceProvider {

    private static final String IDEALO_URL = "https://www.idealo.de";

    private static final String IDEALO_SEARCH_PRODUCT_SELECTOR = "div.resultlist > div.offerList > div";
    private static final String IDEALO_DETAIL_SELECTOR = ".productOffers-listItemOffer > div > a";

    private static String getSearchUrl(int page) {
        return IDEALO_URL + "/preisvergleich/MainSearchProductCategory/100I-1-" + ((page - 1) * 15) + ".html";
    }

    @Override
    public void crawl() throws IOException {
        List<String> searchResult = search("joop");

        for (String s : searchResult) {
            getArticle(s);
        }
    }

    public void getArticle(String productLink) throws IOException {
        Document doc = getDocument(IDEALO_URL + productLink);

        Elements seller = doc.select(IDEALO_DETAIL_SELECTOR);
        log.info("Size " + seller.size());
        for (Element element : seller) {
            String payload = element.attr("data-gtm-payload");

            JSONObject payloadObject = new JSONObject(payload);

            log.info("Name " + payloadObject.getString("product_name"));
            log.info("Price " + payloadObject.getString("product_price"));
            log.info("Shop " + payloadObject.getString("shop_name"));
            log.info("---------------------------------");
        }
    }

    public List<String> search(String query) throws IOException {

        List<String> productLinks = new ArrayList<>();

        var pages = 1;
        var page = 1;
        do {
            val searchURI = UriComponentsBuilder.fromUriString(getSearchUrl(page)).queryParam("q", query).build();

            Document doc = getDocument(searchURI.toString());

            Elements products = doc.select(IDEALO_SEARCH_PRODUCT_SELECTOR);

            log.info("Size " + products.size());
            for (Element element : products) {
                productLinks.add(element.selectFirst("a").attr("href"));
            }

            if (page == 1) {
                String lastPage = doc.select(".pagination-item.unavailable + li > a").first().text();
                pages = Integer.valueOf(lastPage);
            }

            page++;
        } while (page < pages);

        return productLinks;
    }

    private Document getDocument(String url) throws IOException {
        log.info("Requesting " + url);
        Document document = Jsoup.connect(url)
                .userAgent(BrowserConstants.USER_AGENT)
                .followRedirects(true)
                .get();
        log.info("Got Response " + url);

        return document;
    }
}
