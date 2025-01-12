package com.lzpavel.chargecontrol

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.UUID

class ChargerWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

        val LOG_TAG = "ChargerWorker"

    companion object {

        private var workRequestId: UUID? = null
        private var workManager: WorkManager? = null

        fun schedule(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .build()


            val workRequest = OneTimeWorkRequest.Builder(ChargerWorker::class.java)
                .setConstraints(constraints)
                .build()

            workRequestId = workRequest.id

            workManager = WorkManager.getInstance(context)
            workManager!!.enqueue(workRequest)
        }

        fun cancel() {
            if (workManager != null && workRequestId != null) {
                workManager!!.cancelWorkById(workRequestId!!)
                workRequestId = null
                workManager = null
            }
        }
    }

    override fun doWork(): Result {
        Log.d(LOG_TAG, "doWork")
        return Result.success()
    }
}