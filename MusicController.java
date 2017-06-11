package pl.edu.pwr.player.musicplayer;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

/**
 * Created by mlyna on 02.06.2017.
 */

public class MusicController extends MediaController {
    Context c;
    public MusicController(Context context) {
        super(context);
        c=context;
    }

    public void hide(){}

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        int keyCode = event.getKeyCode();
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            ((MainActivity) c).onBackPressed();
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }
}
