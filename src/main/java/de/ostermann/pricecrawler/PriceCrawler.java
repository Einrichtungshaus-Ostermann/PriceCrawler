package de.ostermann.pricecrawler;

import de.ostermann.pricecrawler.provider.PriceProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.Lifecycle;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class PriceCrawler implements Lifecycle {

    private final List<PriceProvider> priceProviders;
    private boolean running;

    public PriceCrawler(List<PriceProvider> priceProviders) {
        this.priceProviders = priceProviders;
    }

    public static void main(String[] args) {
        SpringApplication.run(PriceCrawler.class).start();
    }

    @Override
    public void start() {
        running = true;

        priceProviders.forEach(priceProvider -> {
            try {
                priceProvider.crawl();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        running = false;

    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
