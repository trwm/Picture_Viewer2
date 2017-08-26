package name.weiskirchner.picture_viewer2;

import java.security.Timestamp;
import java.util.Date;

/**
 * Created by michael on 17.06.17.
 */

public class PVimage {

    private int id;
    private String filename;
    private long receivedate;
    private long filesize;
    private String sender;
    private String sendernumber;
    private String text;
    private int newimage;
    private int forevernew;
    private int position;
    private int invisible;


    public PVimage(int id, String filename, long receivedate, long filesize, String sender, String sendernumber, String text, int newimage, int forevernew, int invisible) {
        this.id = id;
        this.filename = filename;
        this.receivedate = receivedate;
        this.filesize = filesize;
        this.sender = sender;
        this.sendernumber = sendernumber;
        this.text = text;
        this.newimage = newimage;
        this.forevernew = forevernew;
        this.invisible = invisible;
    }

    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public long getReceivedate() {
        return receivedate;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSendernumber() {
        return sendernumber;
    }

    public void setSendernumber(String sendernumber) {
        this.sendernumber = sendernumber;
    }

    public String getText() {
        return text;
    }

    public int isNewimage() {
        return newimage;
    }

    public int isForevernew() {
        return forevernew;
    }

    public void setForevernew(int forevernew) {
        this.forevernew = forevernew;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition (int position) {
        this.position = position;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public int isInvisible() {
        return invisible;
    }

    public void setInvisible(int invisible) {
        this.invisible = invisible;
    }
}
