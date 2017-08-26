package name.weiskirchner.picture_viewer2;

/**
 * Created by michael on 09.07.17.
 */

public class WAImage {

    private long receivedate;
    private long filesize;
    private String sendernumber;

    public WAImage(long receivedate, long filesize, String sendernumber) {
        this.receivedate = receivedate;
        this.filesize = filesize;
        this.sendernumber = sendernumber;
    }


    public long getReceivedate() {
        return receivedate;
    }

    public long getFilesize() {
        return filesize;
    }

    public String getSendernumber() {
        return sendernumber;
    }

}
