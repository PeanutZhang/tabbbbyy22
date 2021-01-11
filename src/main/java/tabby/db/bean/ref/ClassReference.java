package tabby.db.bean.ref;

import lombok.Data;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;
import tabby.db.bean.edge.Extend;
import tabby.db.bean.edge.Has;
import tabby.db.bean.edge.Interfaces;
import tabby.db.converter.List2JsonStringConverter;
import tabby.db.converter.Set2JsonStringConverter;

import javax.persistence.*;
import java.util.*;

/**
 * @author wh1t3P1g
 * @since 2020/10/9
 */
@Data
@Entity
@Table(name = "classes")
//@NodeEntity(label="Class")
public class ClassReference {

    @Id
    private String id;
//    @Column(unique = true)
    private String name;
    private String superClass;

    private boolean isInterface = false;
    private boolean hasSuperClass = false;
    private boolean hasInterfaces = false;
    private boolean isInitialed = false;
    private boolean isSerializable = false;
    /**
     * [[name, modifiers, type],...]
     */
    @Column(columnDefinition = "TEXT")
    @Convert(converter = Set2JsonStringConverter.class)
    private Set<String> fields = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    @Convert(converter = List2JsonStringConverter.class)
    private List<String> interfaces = new ArrayList<>();

    // neo4j relationships
    /**
     * 继承边
     */
    @Transient // for mongodb
    @Relationship(type="EXTEND")
    private transient Extend extendEdge = null;

    /**
     * 类成员函数 has边
     * Object A has Method B
     */
    @Transient // for mongodb
    @Relationship(type="HAS", direction = "UNDIRECTED")
    private transient List<Has> hasEdge = new ArrayList<>();

    /**
     * 接口继承边
     * 双向，可以从实现类A到接口B，也可以从接口类B到实现类A
     * A -[:INTERFACE]-> B
     * B -[:INTERFACE]-> A
     */
    @Transient // for mongodb
    @Relationship(type="INTERFACE", direction = "UNDIRECTED")
    private transient Set<Interfaces> interfaceEdge = new HashSet<>();

    public static ClassReference newInstance(String name){
        ClassReference classRef = new ClassReference();
        classRef.setId(UUID.randomUUID().toString());
        classRef.setName(name);
        classRef.setInterfaces(new ArrayList<>());
        classRef.setFields(new HashSet<>());
//        classRef.setAnnotations(new HashSet<>());
        return classRef;
    }

}
