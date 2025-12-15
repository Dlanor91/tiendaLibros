package gm.tienda.libros.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ms-ventas")
public class MsVentasProperties {
    private String url;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}