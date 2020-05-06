import java.util.PropertyPermission;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class ReadProp {
    public static void main(String[] args){
        String javaPath;
        System.out.println("准备输出jdk_path...");
        try{
            SecurityManager s = System.getSecurityManager();
            s.checkPermission(new PropertyPermission("java.home","read"));
            javaPath = System.getProperty("java.home","not specified");
            System.out.println("jdk_path = "+ javaPath);
        }catch (Exception e){
            System.err.println("异常："+e.toString());
        }
    }
}
