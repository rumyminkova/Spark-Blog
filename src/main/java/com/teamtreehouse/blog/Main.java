package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDAO;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import com.teamtreehouse.blog.model.NotFoundException;
import com.teamtreehouse.blog.model.Tags;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.*;

import static spark.Spark.*;

/**
 * Created by Rumy on 5/15/2017.
 */
public class Main {
    private static final String FLASH_MESSAGE_KEY ="flash_message";
    public static void main(String[] args) {
        staticFileLocation("/public");
        BlogDao dao = new SimpleBlogDAO();
        String password = "admin";


        before((req,res)->{
            if(req.cookie("username")!=null){
                req.attribute("username",req.cookie("username"));
            }
        });



        before("/entries/:page/:slug", (req, res) -> {
            if (req.attribute("username")==null){
                String s="/entries/"+req.params("page")+"/"+req.params("slug");
                res.redirect(s+"/sign-in");
                halt();
            }
        });


        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("blogEntries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());


        get("/entries/:slug",(req,res)->{
            Map<String,Object> model=new HashMap<>();
            BlogEntry blogEntry=dao.findEntryBySlug(req.params("slug"));
            model.put("entry",blogEntry);
            return new ModelAndView(model,"detail.hbs");
        },new HandlebarsTemplateEngine());


        post("/entries/:slug",(req,res)->{
            String sl =req.params("slug");
            BlogEntry blogEntry=dao.findEntryBySlug(sl);
            String name = req.queryParams("name");
            String text = req.queryParams("comment");
            Comment comment=new Comment(name,text);
            blogEntry.addComment(comment);
            res.redirect("/entries/"+sl);
            return null;

        });




        get("/entries/new-entry/new-entry", (req, res) -> {
            ArrayList<String> tags= Tags.load();
            Map<String,Object> model=new HashMap<>();
            model.put("tagsList",tags);
            return new ModelAndView(model, "new.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entries/new-entry/new-entry",(req,res)-> {
            String title = req.queryParams("title");
            String body = req.queryParams("body");
            String[] t = req.queryParamsValues("tags");
            BlogEntry blogEntry=new BlogEntry(title);
            blogEntry.setBody(body);
            if (t !=null) {
                ArrayList<String> tags=new ArrayList<>(Arrays.asList(t));
                blogEntry.addTags(tags);
            }
            dao.addEntry(blogEntry);
            res.redirect("/");
            return null;
        });



        get("/entries/edit/:slug", (req, res) -> {
            String sl =req.params("slug");
            BlogEntry blogEntry=dao.findEntryBySlug(sl);
            Map<String,Object> model=new HashMap<>();
            model.put("entry",blogEntry);
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entries/edit/:slug", (req, res) -> {
            String sl =req.params("slug");
            BlogEntry blogEntry=dao.findEntryBySlug(sl);
            String body = req.queryParams("body");
            String title = req.queryParams("title");
            blogEntry.setTitle(title);
            blogEntry.setBody(body);
            res.redirect("/entries/"+sl);
            return null;
        });


        get("/entries/delete/:slug", (req, res) -> {
            String sl =req.params("slug");
            BlogEntry blogEntry=dao.findEntryBySlug(sl);
            dao.removeEntry(blogEntry);
            res.redirect("/");
            return null;
        });


        get("/entries/:page/:slug/sign-in", (req, res) -> {
            Map<String,String> model=new HashMap<>();
            String s="/entries/"+req.params("page")+"/"+req.params("slug");
            model.put("goto-page",s);
            model.put("flashMessage",captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entries/:page/:slug/sign-in",(req,res)->{
            String username = req.queryParams("username");
            String goTo="/entries/"+req.params("page")+"/"+req.params("slug");

            if (username.equals(password)) {
                res.cookie("/","username",username,3600,false);
                res.redirect(goTo);

            }else {
                setFlashMessage(req,"Wrong password!");
                res.redirect(goTo+"/sign-in");
            }
            return null;
        });

        post("/tags/:tag",(req,res)->{
            Map<String,Object> model=new HashMap<>();
            String t =req.params("tag");
            List<BlogEntry> entriesByTag=dao.findEntriesByTag(t);
            model.put("blogEntries",entriesByTag);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());



        exception(NotFoundException.class,(exc, req, res)->{
            res.status(404);
            HandlebarsTemplateEngine engine=new HandlebarsTemplateEngine();
            String html=engine.render(new ModelAndView(null,"not-found.hbs"));
            res.body(html);
        });

    }



    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY,message);
    }

    private static String getFlashMessage(Request req){
        if(req.session(false)==null){
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)){
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request req){
        String message =getFlashMessage(req);
        if(message!=null){
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }

        return message;
    }
}
