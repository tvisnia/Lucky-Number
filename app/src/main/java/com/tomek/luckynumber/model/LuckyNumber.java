package com.tomek.luckynumber.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

import org.jsoup.nodes.Element;

public class LuckyNumber
{
	private static final String URL_STRING = "http://lo-lubaczow.pl/";

	public static String parseHtml(Document main_page)
	{
		Element image = main_page.select("div.grid_1.numerki").first();
		Element img = image.children().first();
		String img_url = img.attr("src");
		return img_url.substring(img_url.lastIndexOf("m")+1, img_url.indexOf("."));
	}
	
	public static int getLucky() throws IOException
	{
		Document main_page = Jsoup.connect(URL_STRING).get();
		String img_number = parseHtml(main_page);
		return Integer.parseInt(img_number);
	}

	public static void printLucky()
	{
		int lucky = 0;
		try
		{
			lucky = getLucky();
		}
		catch(IOException ex)
		{
			System.out.println(ex.toString());
		}
		System.out.println("Your lucky number is " + lucky);
	}

	public static void main(String[] args)
	{
		printLucky();
	}
}
