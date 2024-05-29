package SweetDreams.SweetDreamsHotel;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/image/**") // Define the URL pattern to access images
                .addResourceLocations("file:///C:/Users/Multistrada/Music/Th/webtech/SweetDreamsHotel/SweetDreamsHotel/src/main/resources/roomImages"); // Set the path to your images directory
    }

}
