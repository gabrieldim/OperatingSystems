
import java.io.File;

public class Zadaca2 {

    public static void prebarajDatoteki(String path) {
        File folder = new File(path);
        File []files = folder.listFiles();

        assert files != null;

        for(File f: files) {

            if(f.isDirectory()) {
                prebarajDatoteki(f.getAbsolutePath());
            }

            if(f.isFile() &&
                    (f.getName().endsWith(".jpg") || f.getName().endsWith(".bmp"))) {
                System.out.println(f.getName());
            }
        }

    }

    public static void main(String[] args) {
        String filePath = args[0];
        prebarajDatoteki(filePath);
    }
}
