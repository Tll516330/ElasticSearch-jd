package com.tll.utils;

import com.tll.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tll
 * @create 2020/6/22 14:12
 * 爬取数据
 * 放到Spring容器中  不注入就需要new
 */
@Component
public class HtmlParseUtil {
//    public static void main(String[] args) throws IOException {
//        //链式调用
//        new HtmlParseUtil().parseJD("爱情").forEach(System.out::println);
//    }

    //解析京东
    public List<Content> parseJD(String keywords) throws IOException {
        //https://search.jd.com/Search?keyword=java&enc=utf-8
        String url = "https://search.jd.com/Search?keyword=" + keywords + "&enc=utf-8";
        //解析网页(Jsoup返回的Document就是js 页面对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有在js中使用的方法，这里都可以用
        Element element = document.getElementById("J_goodsList");
        //获取所以的 li标签
        Elements elements = element.getElementsByTag("li");
        //获取元素中的内容，这里的el就是每一个li标签
        ArrayList<Content> goodsList = new ArrayList<>();
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodsList.add(content);

        }
        return goodsList;
    }
}
