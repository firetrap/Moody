package ui;

import com.android.moody.R;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class CardTextView extends TextView {

	public CardTextView(Context context, String text, int visibility,
			boolean clickAble, int color) {
		super(context);
		setText(text);
		setVisibility(visibility);
		// setTextAppearance(context, R.style.CardLightText);
		setClickable(clickAble);
		if (color != 0)
			setTextColor(color);
		setTextSize(18);
		setPadding(10, 10, 10, 10);
//		setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.card_background));
		setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

	}
}
