package com.android.calendars.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.calendars.Constant;
import com.android.calendars.R;
import com.android.calendars.models.DayMonthly;
import com.android.calendars.models.Event;
import com.android.calendars.ui.main.customviews.MonthView;
import java.util.List;

public class MonthItemFragment extends Fragment implements MonthlyCalendar {

  private MonthlyCalendarImpl mMonthlyCalendarImpl;
  private long mDayCode;
  private MonthView monthView;
  private TextView tvTitle;
  private FrameLayout flCalendarRoot;
  private boolean isAddingBG;
  private List<Event> mEventList;

  public static MonthItemFragment newInstance(Bundle bundle) {
    MonthItemFragment monthItemFragment = new MonthItemFragment();
    monthItemFragment.setArguments(bundle);
    return monthItemFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mDayCode = getArguments().getLong(Constant.DAY_CODE);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_month_item, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    monthView = view.findViewById(R.id.month_view_wrapper);
    tvTitle = view.findViewById(R.id.top_value);
    flCalendarRoot = view.findViewById(R.id.fl_calendar_container);
    mMonthlyCalendarImpl = new MonthlyCalendarImpl(this, view.getContext());
    if (getParentFragment() instanceof IMonthListener) {
      mEventList = ((IMonthListener) getParentFragment()).getEventList();
    }
    mMonthlyCalendarImpl.getDays(mDayCode);
  }

  @Override
  public void updateMonthlyCalendar(Context context, String month, final List<DayMonthly> days) {
    tvTitle.setText(month);
    mMonthlyCalendarImpl.makeEvent(mEventList, days);
    monthView.updateDays(days);
    final LayoutInflater inflater = LayoutInflater.from(context);
    monthView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
      if (monthView.getDayWidth() != 0 && monthView.getDayHeight() != 0 && !isAddingBG) {
        isAddingBG = true;
        int dayIndex = 0;
        for (int y = 0; y < 6; y++) {
          for (int x = 0; x < 7; x++) {
            DayMonthly day = days.get(dayIndex);
            float xPos = x * monthView.getDayWidth();
            float yPos = y * monthView.getDayHeight() + monthView.getWeekDaysLetterHeight();
            addBGView(inflater, day, xPos, yPos);
            dayIndex++;
          }
        }
      }
    });
  }

  private void addBGView(LayoutInflater inflater, final DayMonthly day, float x, float y) {
    View bgView = inflater.inflate(R.layout.month_view_background, null);
    ViewGroup.LayoutParams layoutParams = new LayoutParams(0, 0);
    layoutParams.width = (int) monthView.getDayWidth();
    layoutParams.height = (int) monthView.getDayHeight();
    bgView.setLayoutParams(layoutParams);
    bgView.setX(x);
    bgView.setY(y);
    bgView.setOnClickListener(
        v -> {
          if (getParentFragment() instanceof IMonthListener) {
            ((IMonthListener) getParentFragment()).onDayMonthlyClicked(day);
          }
        });
    flCalendarRoot.addView(bgView);
  }
}
