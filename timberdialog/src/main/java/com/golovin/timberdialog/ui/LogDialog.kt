package com.golovin.timberdialog.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.golovin.timberdialog.LogEntry
import com.golovin.timberdialog.LumberYard
import com.golovin.timberdialog.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

private const val CODE_STORAGE_PERMISSION_REQUEST = 173

class LogDialog : AppCompatDialogFragment() {
    private val logAdapter = LogAdapter()

    private lateinit var recyclerView: RecyclerView

    private var disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_log, container, false)

        recyclerView = view.findViewById(R.id.recycler_logs)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initButtons()
    }

    override fun onStart() {
        super.onStart()

        LumberYard.getInstance(activity.applicationContext).let {
            logAdapter.setEntries(it.bufferedLogs())
            recyclerView.scrollToPosition(logAdapter.itemCount - 1)

            disposables += it.logs()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { addLogEntry(it) }
        }
    }

    override fun onResume() {
        super.onResume()

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onStop() {
        super.onStop()

        disposables.clear()
    }

    private fun addLogEntry(entry: LogEntry) {
        logAdapter.addEntry(entry)
        recyclerView.scrollToPosition(logAdapter.itemCount - 1)
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            adapter = logAdapter
        }
    }

    private fun initButtons() {
        view?.let {
            it.findViewById<View>(R.id.button_cancel)?.setOnClickListener { dismiss() }
            it.findViewById<View>(R.id.button_share)?.setOnClickListener {
                if (checkPermissions()) {
                    share()
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(permission)
            requestPermissions(permissions, CODE_STORAGE_PERMISSION_REQUEST)

            return false
        }

        return true
    }

    private fun share() {
        disposables += LumberYard.getInstance(activity.applicationContext)
                .save()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val uri = FileProvider.getUriForFile(activity, activity.packageName, it)

                    val intent = ShareCompat.IntentBuilder.from(activity)
                            .setType("text/plain")
                            .setStream(uri)
                            .intent

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    maybeStartChooser(intent)

                }, { Toast.makeText(activity, R.string.save_error, Toast.LENGTH_SHORT).show() })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != CODE_STORAGE_PERMISSION_REQUEST) return

        when {
            grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED -> share()

            !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ->
                Toast.makeText(context, R.string.no_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private fun maybeStartChooser(intent: Intent) {
        if (hasHandler(activity, intent)) {
            activity.startActivity(Intent.createChooser(intent, null))
        } else {
            Toast.makeText(activity, R.string.no_intent_handler, Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasHandler(context: Context, intent: Intent): Boolean =
            context.packageManager.queryIntentActivities(intent, 0).isNotEmpty()

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }
}