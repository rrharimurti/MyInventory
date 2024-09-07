package MyInventory;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraper {
	//scrape method for autofill
	 public static Lego scrape(String serial_no, Lego lego) {
	        
	        try {
	        	//connect to brickset.com/sets/(insert serial number) to begin web scraping
	        	String url = "https://brickset.com/sets/" + serial_no;
	            Document document = Jsoup.connect(url).get();
	            Elements header = document.select("dt"); //dt html elements represent categories/headers
	            Elements value = document.select("dd"); //dd html elements represent target data
	            //initialize position variables
	            int name_pos = Integer.MAX_VALUE; 
	            int theme_pos = Integer.MAX_VALUE; 
	            int year_pos = Integer.MAX_VALUE; 
	            int pieces_pos = Integer.MAX_VALUE;
	            int i = 0;

	            //find position of categories through looping through header
	            for (Element element : header) {
	            	i++;
	            	if (element.text().equalsIgnoreCase("Name")) {
	            		name_pos = i;
	            	}
	            	if (element.text().equalsIgnoreCase("Theme")) {
	            		theme_pos = i;
	            	}
	            	if (element.text().equalsIgnoreCase("Year Released")) {
	            		year_pos = i;
	            	}
	            	if (element.text().equalsIgnoreCase("Pieces")) {
	            		pieces_pos = i;
	            	}
	            }
	            
	            //extract data by using the positions set
	            i = 0;
	            for (Element element : value) {
	            	i++;
	            	if (i == name_pos) {
	            		lego.setSetName(element.text());
	            	}
	            	if (i == theme_pos) {
	            		lego.setTheme(element.text());
	            	}
	            	if (i == year_pos) {
	            		lego.setYear(Parser.toInt(element.text()));
	            	}
	            	if (i == pieces_pos) {
	            		lego.setPieces(Parser.toInt(element.text()));
	            	}
	            }
	            
	            return lego; //return lego with all the extracted details
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
}

