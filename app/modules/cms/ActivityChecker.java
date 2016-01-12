package modules.cms;

/**
 * Created by Benedikt Linke on 29.11.15.
 */

public class ActivityChecker implements Runnable{

    /**
     * wird nicht verwendet
     */
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000*60*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SessionHolder.getInstance().checkSessions();
        }
    }
}
