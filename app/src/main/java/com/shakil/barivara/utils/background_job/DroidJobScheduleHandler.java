package com.shakil.barivara.utils.background_job;

import android.content.Context;

public interface DroidJobScheduleHandler {

    void onJobSchedulerFire(Context context, String jobName);

}
