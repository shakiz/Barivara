package com.shakil.barivara.utils.background_job;

import static com.shakil.barivara.utils.Constants.mJobName;


import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class JobScheduler {

    Context context;
    WorkManager mWorkManager;
    DroidJobScheduleHandler handler = null;

    public JobScheduler(Context context) {
        this.context = context;
        mWorkManager = WorkManager.getInstance();
    }

    public void onSchedulerRun(String jobName, DroidJobScheduleHandler handler){
        if(handler != null){
            this.handler = handler;
            handler.onJobSchedulerFire(context, jobName);
        }
    }

    public UUID oneTimeJob(Class service, int delay, String jobName) {

        Data data = new Data.Builder()
                .putString(mJobName, jobName)
                .build();

        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.
                Builder(service)
                .addTag(jobName)
                .setInputData(data)
                .setInitialDelay(delay,TimeUnit.MINUTES)
                .build();

        mWorkManager.enqueue(mRequest);
        return mRequest.getId();
    }

    public UUID periodicJobHourly(Class service, int IntervalHour, String jobName) {

        Data data = new Data.Builder()
                .putString(mJobName, jobName)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false) // you can add as many constraints as you want
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build();

        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest
                .Builder(service, IntervalHour, TimeUnit.HOURS)
                .setInitialDelay(IntervalHour, TimeUnit.HOURS)
                .addTag(jobName)
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        mWorkManager.enqueueUniquePeriodicWork(jobName,  ExistingPeriodicWorkPolicy.REPLACE,periodicWork);
        return periodicWork.getId();
    }

    public UUID periodicJobMinute(Class service, int IntervalMinuit, String jobName) {


        Data data = new Data.Builder()
                .putString(mJobName, jobName)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false) // you can add as many constraints as you want
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build();

        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest
                .Builder(service, IntervalMinuit, TimeUnit.MINUTES)
                .setInitialDelay(IntervalMinuit, TimeUnit.MINUTES)
                .addTag(jobName)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        mWorkManager.enqueueUniquePeriodicWork(jobName,  ExistingPeriodicWorkPolicy.REPLACE,periodicWork);

        return periodicWork.getId();
    }

    public UUID periodicJobDay(Class service, int IntervalDay,String jobName) {

        Data data = new Data.Builder()
                .putString(mJobName, jobName)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false) // you can add as many constraints as you want
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build();

        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest
                .Builder(service, IntervalDay, TimeUnit.DAYS)
                .setInitialDelay(IntervalDay, TimeUnit.MINUTES)
                .addTag(jobName)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        mWorkManager.enqueue(periodicWork);
        return periodicWork.getId();
    }

    public boolean isWorkScheduled(String tag) {
        ListenableFuture<List<WorkInfo>> statuses = mWorkManager.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cancelJob(String jobName ) {
        mWorkManager.cancelUniqueWork(jobName);
        mWorkManager.cancelAllWorkByTag(jobName);
    }

    public void cancelAllJob() {
        mWorkManager.cancelAllWork();
    }
}
