package tabby;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import tabby.config.GlobalConfiguration;
import tabby.core.Analyser;
import tabby.util.ApkUtils;
import tabby.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
@EntityScan({"tabby.dal.caching.bean","tabby.dal.neo4j.entity"})
@EnableNeo4jRepositories("tabby.dal.neo4j.repository")
public class App {

    @Autowired
    private Analyser analyser;

    public static void main(String[] args) {

        System.out.println("---------------let's play------------------------");

        if(args.length == 2 && "--config".equals(args[0])){
            GlobalConfiguration.CONFIG_FILE_PATH = FileUtils.getRealPath(args[1]);
        }
        GlobalConfiguration.init();

        String apkJarPath = GlobalConfiguration.CASES_PATH+ File.separator+"targets";
        File targetsJars = new File(apkJarPath);
        if(!targetsJars.exists() || targetsJars.list() == null || targetsJars.list().length == 0){
            System.out.println("uncompress-------jar-----");
            ApkUtils.unzipFile(GlobalConfiguration.APK_PATH,apkJarPath);
            ApkUtils.shellScriptExecutor("d2j.sh",apkJarPath,apkJarPath);
        }

        SpringApplication.run(App.class, args).close();

    }

    public void setLogDebugLevel(){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if(GlobalConfiguration.DEBUG) {
            loggerContext.getLogger("tabby").setLevel(Level.DEBUG);
        }
    }

    @Bean
    CommandLineRunner run(){
        return args -> {
            try{
                GlobalConfiguration.initConfig();
                setLogDebugLevel();
                analyser.run();
            }catch (IllegalArgumentException e){
                log.error(e.getMessage() + ", Please check your settings.properties file.");
            }
            log.info("Done. Bye!");
        };
    }
}
