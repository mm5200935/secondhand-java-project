package app.model;

public class AdvertisementImage {

    private int id;
    private String fileName;
    private String filePath;
    private long fileSize;
    private String fileType;

    public AdvertisementImage() {

    }

    public AdvertisementImage(int id,
                              String fileName,
                              String filePath,
                              long fileSize,
                              String fileType) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

}