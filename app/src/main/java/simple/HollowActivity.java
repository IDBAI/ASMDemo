package simple;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.yy.asm.demo.R;

import hollow.MaskPierceView;

public class HollowActivity extends AppCompatActivity {

    private MaskPierceView maskPierceView;
    private View viewById;
    private View viewById2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hollow3);


        maskPierceView = findViewById(R.id.mask_view);




        viewById = findViewById(R.id.image_view1);
        viewById2 = findViewById(R.id.image_view2);

    }


    @Override
    protected void onResume() {
        super.onResume();

        maskPierceView.addHollowAnchor(new Point(100, 200), 100, 200, 10);

        maskPierceView.invalid();
    }
}