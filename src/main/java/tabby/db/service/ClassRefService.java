package tabby.db.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tabby.config.GlobalConfiguration;
import tabby.db.bean.ref.ClassReference;
import tabby.db.repository.h2.ClassRepository;
import tabby.db.repository.neo4j.ClassRefRepository;
import tabby.db.repository.neo4j.MethodRefRepository;
import tabby.util.FileUtils;

import java.util.List;


/**
 * @author wh1t3P1g
 * @since 2020/10/10
 */
@Slf4j
@Service
public class ClassRefService {

    @Autowired
    private ClassRefRepository classRefRepository;
    @Autowired
    private MethodRefRepository methodRefRepository;
    @Autowired
    private ClassRepository classRepository;

    public void clear(){
        classRefRepository.clearAll();
    }

    public void importClassRef(){
        if(FileUtils.fileExists(GlobalConfiguration.CLASSES_CACHE_PATH)){
            classRefRepository.loadClassRefFromCSV(GlobalConfiguration.CLASSES_CACHE_PATH);
        }
    }

    public void buildEdge(){
        if(FileUtils.fileExists(GlobalConfiguration.EXTEND_RELATIONSHIP_CACHE_PATH)){
            log.info("Save Extend relationship");
            classRefRepository.loadExtendEdgeFromCSV(GlobalConfiguration.EXTEND_RELATIONSHIP_CACHE_PATH);
        }
        if(FileUtils.fileExists(GlobalConfiguration.INTERFACE_RELATIONSHIP_CACHE_PATH)){
            log.info("Save Interface relationship");
            classRefRepository.loadInterfacesEdgeFromCSV(GlobalConfiguration.INTERFACE_RELATIONSHIP_CACHE_PATH);
        }
        if(FileUtils.fileExists(GlobalConfiguration.HAS_RELATIONSHIP_CACHE_PATH)){
            log.info("Save Has relationship");
            classRefRepository.loadHasEdgeFromCSV(GlobalConfiguration.HAS_RELATIONSHIP_CACHE_PATH);
        }
        if(FileUtils.fileExists(GlobalConfiguration.CALL_RELATIONSHIP_CACHE_PATH)){
            log.info("Save Call relationship");
            methodRefRepository.loadCallEdgeFromCSV(GlobalConfiguration.CALL_RELATIONSHIP_CACHE_PATH);
        }
        if(FileUtils.fileExists(GlobalConfiguration.ALIAS_RELATIONSHIP_CACHE_PATH)){
            log.info("Save Alias relationship");
            methodRefRepository.loadAliasEdgeFromCSV(GlobalConfiguration.ALIAS_RELATIONSHIP_CACHE_PATH);
        }
    }

    @Cacheable("classes")
    public ClassReference getClassRefByName(String name){
        return classRepository.findClassReferenceByName(name);
    }

    @CacheEvict(value = "classes", allEntries = true)
    public void clearCache(){
        log.info("All classes cache cleared!");
    }

    public void save(ClassReference ref){
        classRepository.save(ref);
    }

    public void save(Iterable<ClassReference> refs){
        classRepository.saveAll(refs);
    }

    public void save2Csv(){
        classRepository.save2Csv(GlobalConfiguration.CLASSES_CACHE_PATH);
    }

    public List<ClassReference> loadNecessaryClassRefs(){
        return classRepository.findAllNecessaryClassRefs();
    }

}
