package de.ostermann.pricecrawler.provider;

import java.io.IOException;

public interface PriceProvider {
    void crawl() throws IOException;
}
