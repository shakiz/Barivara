package com.shakil.barivara.utils.background_job;

import static com.shakil.barivara.utils.Constants.mJobName;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.shakil.barivara.utils.handlers.JobScheduleHandler;


public class JobScheduleService extends Worker {

    public JobScheduleService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        JobScheduler scheduler = new JobScheduler(getApplicationContext());
        scheduler.onSchedulerRun(getInputData().getString(mJobName), new JobScheduleHandler());
        return Result.success();
    }

}
