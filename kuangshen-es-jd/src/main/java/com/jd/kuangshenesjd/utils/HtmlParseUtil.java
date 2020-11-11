package com.jd.kuangshenesjd.utils;

import com.jd.kuangshenesjd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        //获取请求https://search.jd.com/Search?keyword=java
        //不能获取Ajax，需要模拟浏览器才可以
        String url = "https://search.jd.com/Search?keyword=java";
        //解析网页，Jsoup返回的Document就是Document对象
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有Js能用的方法这里都可以用
        Element element = document.getElementById("J_goodsList");
        System.out.println(element.html());
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        for (Element e1 : elements) {
            String img = e1.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = e1.getElementsByClass("p-price").eq(0).text();
            String name = e1.getElementsByClass("p-name").eq(0).text();
            System.out.println("===================");
            System.out.println(name);
            System.out.println(price);
            System.out.println(img);
        }
    }
/*    @Test
    public void JdStart() throws IOException{
        new HtmlParseUtil().parseJD("huwei").forEach(System.out::println);
    }*/
    //提取方法
    public List<Content> parseJD(String keywords) throws IOException {
        List<Content> goodsList = new ArrayList<>();
        //获取请求https://search.jd.com/Search?keyword=java
        //不能获取Ajax，需要模拟浏览器才可以
        String url = "https://search.jd.com/Search?keyword=" + keywords;
        //解析网页，Jsoup返回的Document就是Document对象
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有Js能用的方法这里都可以用
        Element element = document.getElementById("J_goodsList");
        //System.out.println(element.html());
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        for (Element e1 : elements) {
            String img = e1.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = e1.getElementsByClass("p-price").eq(0).text();
            String name = e1.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(name);

            goodsList.add(content);
        }
        return goodsList;
    }

}
