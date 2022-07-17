package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.ValueAlreadyExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.Tag
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.data.ModuleSP.data_source
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.ResourcesBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.requests.CancellableRequest
import com.github.kittinunf.fuel.gson.responseObject
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Resources : AppCompatActivity() {
    enum class DownloadState {
        Downloading, Succeed, Failed
    }
    companion object {
        val downloadTasks : HashMap<Int, List<CancellableRequest>> = HashMap()
        val downloadStates : HashMap<Int, Triple<MutableList<Enum<DownloadState>>, MutableList<Long>, MutableList<Long>>> = HashMap()
    }

    private lateinit var viewBinding: ResourcesBinding
    private var toast: Toast? = null
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ResourcesBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        context = this

        lifecycleScope.launch(Dispatchers.Main) {
            refresh()

            viewBinding.resourcesVconsoleDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/vconsole")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesVconsoleVersion.adapter = adapter
                        viewBinding.resourcesVconsoleVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesVconsoleDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesVconsolePluginSourcesDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/vconsole-sources")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesVconsolePluginSourcesVersion.adapter = adapter
                        viewBinding.resourcesVconsolePluginSourcesVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesVconsolePluginSourcesDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_sources)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_sources)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesVconsolePluginStatsDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/vconsole-stats-plugin")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesVconsolePluginStatsVersion.adapter = adapter
                        viewBinding.resourcesVconsolePluginStatsVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesVconsolePluginStatsDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_stats)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_stats)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesVconsolePluginVueDevtoolsDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/vue-vconsole-devtools")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesVconsolePluginVueDevtoolsVersion.adapter = adapter
                        viewBinding.resourcesVconsolePluginVueDevtoolsVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesVconsolePluginVueDevtoolsDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_vue_devtools)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_vue_devtools)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesVconsolePluginOutputlogDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/vconsole-outputlog-plugin")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesVconsolePluginOutputlogVersion.adapter = adapter
                        viewBinding.resourcesVconsolePluginOutputlogVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesVconsolePluginOutputlogDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_outputlog)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_outputlog)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaVersion.adapter = adapter
                        viewBinding.resourcesErudaVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginFpsDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-fps")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginFpsVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginFpsVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginFpsDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_fps)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_fps)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginFeaturesDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-features")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginFeaturesVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginFeaturesVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginFeaturesDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_features)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_features)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginTimingDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-timing")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginTimingVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginTimingVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginTimingDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_timing)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_timing)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginMemoryDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-memory")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginMemoryVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginMemoryVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginMemoryDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_memory)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_memory)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginCodeDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-code")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginCodeVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginCodeVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginCodeDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_code)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_code)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginBenchmarkDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-benchmark")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginBenchmarkVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginBenchmarkVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginBenchmarkDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_benchmark)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_benchmark)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginGeolocationDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-geolocation")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginGeolocationVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginGeolocationVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginGeolocationDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_geolocation)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_geolocation)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginDomDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-dom")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginDomVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginDomVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginDomDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_dom)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_dom)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginOrientationDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-orientation")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginOrientationVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginOrientationVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginOrientationDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_orientation)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_orientation)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesErudaPluginTouchesDownload.isEnabled = false
            Fuel.get("https://data.jsdelivr.com/v1/package/npm/eruda-touches")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions> { _, _, result ->
                    result.fold({
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it.versions)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesErudaPluginTouchesVersion.adapter = adapter
                        viewBinding.resourcesErudaPluginTouchesVersion.setSelection(adapter.getPosition(it.tags.latest))
                        viewBinding.resourcesErudaPluginTouchesDownload.isEnabled = true
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_touches)), e)
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_touches)), Toast.LENGTH_SHORT)
                        toast!!.show()
                    })
                }

            viewBinding.resourcesNebulaucsdkDownload.isEnabled = false
            Fuel.get("${modulePrefs("module").get(data_source)}/resources/nebulaucsdk/metadata.json")
                .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.Metadata> { _, _, result ->
                    result.fold({ metadata ->
                        metadata.versions
                    }, { e ->
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.nebulaucsdk)), e)
                        null
                    }).also {
                        if (it == null) {
                            toast?.cancel()
                            toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.nebulaucsdk)), Toast.LENGTH_SHORT)
                            toast!!.show()
                            return@also
                        }
                        val adapter = ArrayAdapter(context, R.layout.component_spinneritem, it)
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        viewBinding.resourcesNebulaucsdkVersion.adapter = adapter
                        viewBinding.resourcesNebulaucsdkVersion.setSelection(0)
                        viewBinding.resourcesNebulaucsdkDownload.isEnabled = true
                    }
                }
        }

        viewBinding.resourcesToolbarBack.setOnClickListener {
            finish()
        }
        viewBinding.resourcesVconsoleCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginSourcesCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginStatsCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginVueDevtoolsCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginOutputlogCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginFpsCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginFeaturesCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginTimingCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginMemoryCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginCodeCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginBenchmarkCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginGeolocationCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginDomCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginOrientationCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginTouchesCard.setOnClickListener { tips() }
        viewBinding.resourcesNebulaucsdkCard.setOnClickListener { tips() }

        fun downloadResources (
            view: View,
            progressBar: android.widget.ProgressBar,
            progressText: android.widget.TextView,
            downloadButton: android.widget.TextView,
            name: String,
            version: String,
            hashSetPref: PrefsData<HashSet<String>>,
            tasks: List<Pair<String, String>>,
            base64: Boolean = false,
        ) {
            downloadTasks[view.id].also { downloadTask ->
                if (downloadTask != null) {
                    downloadStates.remove(view.id)
                    downloadTasks.remove(view.id)

                    downloadTask.forEach { it.cancel() }

                    toast?.cancel()
                    toast = Toast.makeText(context, getString(R.string.download_canceled), Toast.LENGTH_SHORT)
                    toast!!.show()

                    progressBar.visibility = View.GONE
                    progressText.visibility = View.GONE

                    downloadButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableCompat(R.drawable.ic_round_cloud_download_24), null, null, null)
                    downloadButton.tooltipText = getString(R.string.download)
                    downloadButton.contentDescription = getString(R.string.download)

                    return
                }
            }

            downloadButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableCompat(R.drawable.ic_round_cancel_24), null, null, null)
            downloadButton.contentDescription = getString(R.string.cancel)
            downloadButton.tooltipText = getString(R.string.cancel)


            progressBar.visibility = View.VISIBLE
            progressText.visibility = View.VISIBLE

            progressBar.progress = 0
            progressText.text = ""

            toast?.cancel()
            toast = Toast.makeText(context, getString(R.string.download_started), Toast.LENGTH_SHORT)
            toast!!.show()

            downloadStates[view.id] = Triple(
                MutableList(tasks.size) { DownloadState.Downloading },
                MutableList(tasks.size) { 0 },
                MutableList(tasks.size) { 0 }
            )

            downloadTasks[view.id] = tasks.mapIndexed { i, task ->
                Fuel.get(task.first)
                    .responseProgress { readBytes, totalBytes ->
                        val downloadState = downloadStates[view.id] ?: return@responseProgress

                        downloadState.second[i] = readBytes
                        downloadState.third[i] = totalBytes

                        // 统一处理
                        val allReadBytes = downloadState.second.sum()
                        val allTotalBytes = downloadState.third.sum()
                        val progress = allReadBytes.toFloat() / allTotalBytes.toFloat() * 100
                        lifecycleScope.launch(Dispatchers.Main) {
                            progressBar.progress = (progress * 10).toInt()
                            progressText.text = "${allReadBytes.autoUnitByte()}/${allTotalBytes.autoUnitByte()} (${progress.round(2)}%)"
                        }
                    }
                    .response { _, _, result ->
                        val downloadState = downloadStates[view.id] ?: return@response

                        result.fold({
                            with(modulePrefs) {
                                name("resources_${name}_${version}")
                                putString(task.second, if (base64) Base64.encodeToString(it, Base64.NO_WRAP) else String(it))
                                downloadState.first[i] = DownloadState.Succeed
                            }
                        }, { e ->
                            downloadState.first[i] = DownloadState.Failed
                            Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                        })

                        // 统一处理
                        if (!downloadState.first.contains(DownloadState.Downloading)) { // 所有下载任务已结束
                            if (!downloadState.first.contains(DownloadState.Failed)) { // 所有下载任务已成功
                                with(modulePrefs) {
                                    name("resources")
                                    try { put(hashSetPref, version) } catch (_: ValueAlreadyExistedInSet) {}
                                }
                                toast?.cancel()
                                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                                toast!!.show()
                                refresh()
                            } else { // 有失败的下载任务
                                toast?.cancel()
                                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                                toast!!.show()
                            }
                            downloadStates.remove(view.id)
                            downloadTasks.remove(view.id)
                            progressBar.visibility = View.GONE
                            progressText.visibility = View.GONE
                            downloadButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableCompat(R.drawable.ic_round_cloud_download_24), null, null, null)
                            downloadButton.tooltipText = getString(R.string.download)
                            downloadButton.contentDescription = getString(R.string.download)

                        }
                    }
            }
        }

        viewBinding.resourcesVconsoleDownload.setOnClickListener {
            val version = viewBinding.resourcesVconsoleVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesVconsoleProgressBar,
                viewBinding.resourcesVconsoleProgressText,
                viewBinding.resourcesVconsoleDownload,
                "vConsole",
                version,
                ResourcesSP.vConsole_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/vconsole@$version/dist/vconsole.min.js", "vConsole"),
                ),
            )
        }
        viewBinding.resourcesVconsolePluginSourcesDownload.setOnClickListener {
            val version = viewBinding.resourcesVconsolePluginSourcesVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesVconsolePluginSourcesProgressBar,
                viewBinding.resourcesVconsolePluginSourcesProgressText,
                viewBinding.resourcesVconsolePluginSourcesDownload,
                "vConsole_plugin_sources",
                version,
                ResourcesSP.vConsole_plugin_sources_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/vconsole-sources@$version/dist/vconsole-sources.min.js", "vConsole_plugin_sources"),
                ),
            )
        }
        viewBinding.resourcesVconsolePluginStatsDownload.setOnClickListener {
            val version = viewBinding.resourcesVconsolePluginStatsVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesVconsolePluginStatsProgressBar,
                viewBinding.resourcesVconsolePluginStatsProgressText,
                viewBinding.resourcesVconsolePluginStatsDownload,
                "vConsole_plugin_stats",
                version,
                ResourcesSP.vConsole_plugin_stats_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/vconsole-stats-plugin@$version/dist/vconsole-stats-plugin.min.js", "vConsole_plugin_stats"),
                ),
            )
        }
        viewBinding.resourcesVconsolePluginVueDevtoolsDownload.setOnClickListener {
            val version = viewBinding.resourcesVconsolePluginVueDevtoolsVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesVconsolePluginVueDevtoolsProgressBar,
                viewBinding.resourcesVconsolePluginVueDevtoolsProgressText,
                viewBinding.resourcesVconsolePluginVueDevtoolsDownload,
                "vConsole_plugin_vue_devtools",
                version,
                ResourcesSP.vConsole_plugin_vue_devtools_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/vue-vconsole-devtools@$version/dist/vue_plugin.js", "vConsole_plugin_vue_devtools"),
                ),
            )
        }
        viewBinding.resourcesVconsolePluginOutputlogDownload.setOnClickListener {
            val version = viewBinding.resourcesVconsolePluginOutputlogVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesVconsolePluginOutputlogProgressBar,
                viewBinding.resourcesVconsolePluginOutputlogProgressText,
                viewBinding.resourcesVconsolePluginOutputlogDownload,
                "vConsole_plugin_outputlog",
                version,
                ResourcesSP.vConsole_plugin_outputlog_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/vconsole-outputlog-plugin@$version/dist/vconsole-outputlog-plugin.min.js", "vConsole_plugin_outputlog"),
                ),
            )
        }
        viewBinding.resourcesErudaDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaProgressBar,
                viewBinding.resourcesErudaProgressText,
                viewBinding.resourcesErudaDownload,
                "eruda",
                version,
                ResourcesSP.eruda_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda@$version/eruda.js", "eruda"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginFpsDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginFpsVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginFpsProgressBar,
                viewBinding.resourcesErudaPluginFpsProgressText,
                viewBinding.resourcesErudaPluginFpsDownload,
                "eruda_plugin_fps",
                version,
                ResourcesSP.eruda_plugin_fps_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-fps@$version/eruda-fps.min.js", "eruda_plugin_fps"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginFeaturesDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginFeaturesVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginFeaturesProgressBar,
                viewBinding.resourcesErudaPluginFeaturesProgressText,
                viewBinding.resourcesErudaPluginFeaturesDownload,
                "eruda_plugin_features",
                version,
                ResourcesSP.eruda_plugin_features_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-features@$version/eruda-features.min.js", "eruda_plugin_features"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginTimingDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginTimingVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginTimingProgressBar,
                viewBinding.resourcesErudaPluginTimingProgressText,
                viewBinding.resourcesErudaPluginTimingDownload,
                "eruda_plugin_timing",
                version,
                ResourcesSP.eruda_plugin_timing_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-timing@$version/eruda-timing.min.js", "eruda_plugin_timing"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginMemoryDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginMemoryVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginMemoryProgressBar,
                viewBinding.resourcesErudaPluginMemoryProgressText,
                viewBinding.resourcesErudaPluginMemoryDownload,
                "eruda_plugin_memory",
                version,
                ResourcesSP.eruda_plugin_memory_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-memory@$version/eruda-memory.min.js", "eruda_plugin_memory"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginCodeDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginCodeVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginCodeProgressBar,
                viewBinding.resourcesErudaPluginCodeProgressText,
                viewBinding.resourcesErudaPluginCodeDownload,
                "eruda_plugin_code",
                version,
                ResourcesSP.eruda_plugin_code_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-code@$version/eruda-code.min.js", "eruda_plugin_code"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginBenchmarkDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginBenchmarkVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginBenchmarkProgressBar,
                viewBinding.resourcesErudaPluginBenchmarkProgressText,
                viewBinding.resourcesErudaPluginBenchmarkDownload,
                "eruda_plugin_benchmark",
                version,
                ResourcesSP.eruda_plugin_benchmark_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-benchmark@$version/eruda-benchmark.min.js", "eruda_plugin_benchmark"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginGeolocationDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginGeolocationVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginGeolocationProgressBar,
                viewBinding.resourcesErudaPluginGeolocationProgressText,
                viewBinding.resourcesErudaPluginGeolocationDownload,
                "eruda_plugin_geolocation",
                version,
                ResourcesSP.eruda_plugin_geolocation_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-geolocation@$version/eruda-geolocation.min.js", "eruda_plugin_geolocation"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginDomDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginDomVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginDomProgressBar,
                viewBinding.resourcesErudaPluginDomProgressText,
                viewBinding.resourcesErudaPluginDomDownload,
                "eruda_plugin_dom",
                version,
                ResourcesSP.eruda_plugin_dom_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-dom@$version/eruda-dom.min.js", "eruda_plugin_dom"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginOrientationDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginOrientationVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginOrientationProgressBar,
                viewBinding.resourcesErudaPluginOrientationProgressText,
                viewBinding.resourcesErudaPluginOrientationDownload,
                "eruda_plugin_orientation",
                version,
                ResourcesSP.eruda_plugin_orientation_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-orientation@$version/eruda-orientation.min.js", "eruda_plugin_orientation"),
                ),
            )
        }
        viewBinding.resourcesErudaPluginTouchesDownload.setOnClickListener {
            val version = viewBinding.resourcesErudaPluginTouchesVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesErudaPluginTouchesProgressBar,
                viewBinding.resourcesErudaPluginTouchesProgressText,
                viewBinding.resourcesErudaPluginTouchesDownload,
                "eruda_plugin_touches",
                version,
                ResourcesSP.eruda_plugin_touches_versions,
                listOf(
                    Pair("https://cdn.jsdelivr.net/npm/eruda-touches@$version/eruda-touches.min.js", "eruda_plugin_touches"),
                ),
            )
        }

        viewBinding.resourcesNebulaucsdkDownload.setOnClickListener {
            val version = viewBinding.resourcesNebulaucsdkVersion.selectedItem as String
            downloadResources(
                it,
                viewBinding.resourcesNebulaucsdkProgressBar,
                viewBinding.resourcesNebulaucsdkProgressText,
                viewBinding.resourcesNebulaucsdkDownload,
                "nebulaUCSDK",
                version,
                ResourcesSP.nebulaUCSDK_versions,
                listOf(
                    Pair("${modulePrefs("module").get(data_source)}/resources/nebulaucsdk/$version/arm64-v8a.so", "nebulaUCSDK_arm64-v8a"),
                    Pair("${modulePrefs("module").get(data_source)}/resources/nebulaucsdk/$version/armeabi-v7a.so", "nebulaUCSDK_armeabi-v7a"),
                ),
                base64 = true
            )
        }
    }

    private fun refresh() {
        val vConsoleVersions: HashSet<String>
        val vConsolePluginSourcesVersions: HashSet<String>
        val vConsolePluginStatsVersions: HashSet<String>
        val vConsolePluginVueDevtoolsVersions: HashSet<String>
        val vConsolePluginOutputlogVersions: HashSet<String>
        val erudaVersions: HashSet<String>
        val erudaPluginFpsVersions: HashSet<String>
        val erudaPluginFeaturesVersions: HashSet<String>
        val erudaPluginTimingVersions: HashSet<String>
        val erudaPluginMemoryVersions: HashSet<String>
        val erudaPluginCodeVersions: HashSet<String>
        val erudaPluginBenchmarkVersions: HashSet<String>
        val erudaPluginGeolocationVersions: HashSet<String>
        val erudaPluginDomVersions: HashSet<String>
        val erudaPluginOrientationVersions: HashSet<String>
        val erudaPluginTouchesVersions: HashSet<String>
        val nebulaUCSDKVersions: HashSet<String>
        with(modulePrefs("resources")) {
            vConsoleVersions = getSet(ResourcesSP.vConsole_versions)
            vConsolePluginSourcesVersions = getSet(ResourcesSP.vConsole_plugin_sources_versions)
            vConsolePluginStatsVersions = getSet(ResourcesSP.vConsole_plugin_stats_versions)
            vConsolePluginVueDevtoolsVersions = getSet(ResourcesSP.vConsole_plugin_vue_devtools_versions)
            vConsolePluginOutputlogVersions = getSet(ResourcesSP.vConsole_plugin_outputlog_versions)
            erudaVersions = getSet(ResourcesSP.eruda_versions)
            erudaPluginFpsVersions = getSet(ResourcesSP.eruda_plugin_fps_versions)
            erudaPluginFeaturesVersions = getSet(ResourcesSP.eruda_plugin_features_versions)
            erudaPluginTimingVersions = getSet(ResourcesSP.eruda_plugin_timing_versions)
            erudaPluginMemoryVersions = getSet(ResourcesSP.eruda_plugin_memory_versions)
            erudaPluginCodeVersions = getSet(ResourcesSP.eruda_plugin_code_versions)
            erudaPluginBenchmarkVersions = getSet(ResourcesSP.eruda_plugin_benchmark_versions)
            erudaPluginGeolocationVersions = getSet(ResourcesSP.eruda_plugin_geolocation_versions)
            erudaPluginDomVersions = getSet(ResourcesSP.eruda_plugin_dom_versions)
            erudaPluginOrientationVersions = getSet(ResourcesSP.eruda_plugin_orientation_versions)
            erudaPluginTouchesVersions = getSet(ResourcesSP.eruda_plugin_touches_versions)
            nebulaUCSDKVersions = getSet(ResourcesSP.nebulaUCSDK_versions)
        }
        viewBinding.resourcesVconsoleLocal.apply {
            removeAllViews()
            vConsoleVersions.forEach { vConsoleVersion ->
                addView(Tag(context).apply {
                    text = vConsoleVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole) && get(AppSP.vConsole_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_versions, version)
                            name("resources_vConsole_$version")
                            remove("vConsole")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginSourcesLocal.apply {
            removeAllViews()
            vConsolePluginSourcesVersions.forEach { vConsolePluginSourcesVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginSourcesVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_sources) && get(AppSP.vConsole_plugin_sources_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_sources_versions, version)
                            name("resources_vConsole_plugin_sources_$version")
                            remove("vConsole_plugin_sources")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginStatsLocal.apply {
            removeAllViews()
            vConsolePluginStatsVersions.forEach { vConsolePluginStatsVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginStatsVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_stats) && get(AppSP.vConsole_plugin_stats_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_stats_versions, version)
                            name("resources_vConsole_plugin_stats_$version")
                            remove("vConsole_plugin_stats")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginVueDevtoolsLocal.apply {
            removeAllViews()
            vConsolePluginVueDevtoolsVersions.forEach { vConsolePluginVueDevtoolsVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginVueDevtoolsVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_vue_devtools) && get(AppSP.vConsole_plugin_vue_devtools_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_vue_devtools_versions, version)
                            name("resources_vConsole_plugin_vue_devtools_$version")
                            remove("vConsole_plugin_vue_devtools")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginOutputlogLocal.apply {
            removeAllViews()
            vConsolePluginOutputlogVersions.forEach { vConsolePluginOutputlogVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginOutputlogVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_outputlog) && get(AppSP.vConsole_plugin_outputlog_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_outputlog_versions, version)
                            name("resources_vConsole_plugin_outputlog_$version")
                            remove("vConsole_plugin_outputlog")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaLocal.apply {
            removeAllViews()
            erudaVersions.forEach { erudaVersion ->
                addView(Tag(context).apply {
                    text = erudaVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda) && get(AppSP.eruda_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_versions, version)
                            name("resources_eruda_$version")
                            remove("eruda")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginFpsLocal.apply {
            removeAllViews()
            erudaPluginFpsVersions.forEach { erudaPluginFpsVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginFpsVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_fps) && get(AppSP.eruda_plugin_fps_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_fps_versions, version)
                            name("resources_eruda_plugin_fps_$version")
                            remove("eruda_plugin_fps")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginFeaturesLocal.apply {
            removeAllViews()
            erudaPluginFeaturesVersions.forEach { erudaPluginFeaturesVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginFeaturesVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_features) && get(AppSP.eruda_plugin_features_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_features_versions, version)
                            name("resources_eruda_plugin_features_$version")
                            remove("eruda_plugin_features")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginTimingLocal.apply {
            removeAllViews()
            erudaPluginTimingVersions.forEach { erudaPluginTimingVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginTimingVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_timing) && get(AppSP.eruda_plugin_timing_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_timing_versions, version)
                            name("resources_eruda_plugin_timing_$version")
                            remove("eruda_plugin_timing")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginMemoryLocal.apply {
            removeAllViews()
            erudaPluginMemoryVersions.forEach { erudaPluginMemoryVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginMemoryVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_memory) && get(AppSP.eruda_plugin_memory_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_memory_versions, version)
                            name("resources_eruda_plugin_memory_$version")
                            remove("eruda_plugin_memory")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginCodeLocal.apply {
            removeAllViews()
            erudaPluginCodeVersions.forEach { erudaPluginCodeVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginCodeVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_code) && get(AppSP.eruda_plugin_code_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_code_versions, version)
                            name("resources_eruda_plugin_code_$version")
                            remove("eruda_plugin_code")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginBenchmarkLocal.apply {
            removeAllViews()
            erudaPluginBenchmarkVersions.forEach { erudaPluginBenchmarkVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginBenchmarkVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_benchmark) && get(AppSP.eruda_plugin_benchmark_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_benchmark_versions, version)
                            name("resources_eruda_plugin_benchmark_$version")
                            remove("eruda_plugin_benchmark")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginGeolocationLocal.apply {
            removeAllViews()
            erudaPluginGeolocationVersions.forEach { erudaPluginGeolocationVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginGeolocationVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_geolocation) && get(AppSP.eruda_plugin_geolocation_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_geolocation_versions, version)
                            name("resources_eruda_plugin_geolocation_$version")
                            remove("eruda_plugin_geolocation")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginDomLocal.apply {
            removeAllViews()
            erudaPluginDomVersions.forEach { erudaPluginDomVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginDomVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_dom) && get(AppSP.eruda_plugin_dom_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_dom_versions, version)
                            name("resources_eruda_plugin_dom_$version")
                            remove("eruda_plugin_dom")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginOrientationLocal.apply {
            removeAllViews()
            erudaPluginOrientationVersions.forEach { erudaPluginOrientationVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginOrientationVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_orientation) && get(AppSP.eruda_plugin_orientation_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_orientation_versions, version)
                            name("resources_eruda_plugin_orientation_$version")
                            remove("eruda_plugin_orientation")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginTouchesLocal.apply {
            removeAllViews()
            erudaPluginTouchesVersions.forEach { erudaPluginTouchesVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginTouchesVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_touches) && get(AppSP.eruda_plugin_touches_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_touches_versions, version)
                            name("resources_eruda_plugin_touches_$version")
                            remove("eruda_plugin_touches")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesNebulaucsdkLocal.apply {
            removeAllViews()
            nebulaUCSDKVersions.forEach { nebulaUCSDKVersion ->
                addView(Tag(context).apply {
                    text = nebulaUCSDKVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.nebulaUCSDK) && get(AppSP.nebulaUCSDK_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.nebulaUCSDK_versions, version)
                            name("resources_nebulaUCSDK_$version")
                            remove("nebulaUCSDK_arm64-v8a")
                            remove("nebulaUCSDK_armeabi-v7a")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
    }

    private fun tips() {
        toast?.cancel()
        toast = Toast.makeText(context, getString(R.string.please_click_the_download_button_instead_of_here_long_press_on_a_version_tag_to_delete_it), Toast.LENGTH_SHORT)
        toast!!.show()
    }
}