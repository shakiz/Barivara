package com.shakil.barivara.utils.handlers;

import android.content.Context;

import com.shakil.barivara.R;
import com.shakil.barivara.utils.Constants;
import com.shakil.barivara.utils.background_job.DroidJobScheduleHandler;

public class JobScheduleHandler implements DroidJobScheduleHandler {

    @Override
    public void onJobSchedulerFire(Context context, String jobName) {
        switch (jobName) {
            case Constants.IsStartOfMonth:
                break;
        }
    }
}
