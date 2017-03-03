package tikape.foorumirunko;

import java.util.*;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.foorumirunko.database.*;
import tikape.foorumirunko.domain.*;

public class Main {

    public static void main(String[] args) throws Exception {
        /* //herokuun siirtymiseen liittyvä portinhakusetti
         if (System.getenv("PORT") != null) {
         port(Integer.valueOf(System.getenv("PORT")));
         }
         */

        Database database = new Database("jdbc:sqlite:foorumi.db");
        database.dropAllTables();
        database.init();
        database.instantiateTestData();

        KayttajaDao kayttajaDao = new KayttajaDao(database);
        ViestiDao viestiDao = new ViestiDao(database);
        AlueDao alueDao = new AlueDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        post("/", (req, res) -> {
            String nimi = req.queryParams("nimi");
            
            if (nimi != null) {
                int key = alueDao.kuinkaMontaAluetta() + 1;
                Alue a = new Alue(key, nimi);
                alueDao.InsertOne(a);
            }
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/kayttajat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kayttajat", kayttajaDao.findAll());

            return new ModelAndView(map, "kayttajat");
        }, new ThymeleafTemplateEngine());

        get("/kayttaja/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kayttaja", kayttajaDao.findOne(req.params("id")));

            return new ModelAndView(map, "kayttaja");
        }, new ThymeleafTemplateEngine());
    }
}
