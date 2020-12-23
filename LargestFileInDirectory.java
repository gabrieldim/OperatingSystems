import java.io.File;

public class Zadaca1 {
    public static void main(String[] args) {
        String filePath = "/home/user1/OSTest";

        File folder = new File(filePath);
        File []files = folder.listFiles();

        File maxSizeFile = null;
        long maxSize = 0;

        for(File f : files) {
            if(f.isFile()) {
                maxSizeFile = f;
                maxSize = f.length();
                break;
            }
        }

        for(int i=0; i<files.length; i++) {

            if(files[i].isDirectory()) {
                continue;
            }

            if(files[i].length() > maxSize) {
                maxSize = files[i].length();
                maxSizeFile = files[i];
            }
        }

        System.out.println(maxSizeFile.getName());

    }
}
