package com.gyf.cactus.sample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.gyf.cactus.ext.cactusUnregister
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @author geyifeng
 * @date 2019-08-28 17:22
 */
class MainActivity : BaseActivity() {

    private var times = 0L

    private val list = listOf(
            Pair("今日头条", "抖音全世界通用"),
            Pair("微博", "赵丽颖吐槽中餐厅"),
            Pair("绿洲", "今天又是美好的一天"),
            Pair("QQ", "好友申请"),
            Pair("微信", "在吗？"),
            Pair("百度地图", "新的路径规划"),
            Pair("墨迹天气", "明日大风，注意出行"),
            Pair("信息", "1条文本信息"),
            Pair("手机天猫", "你关注的宝贝降价啦")
    )

    companion object {
        private const val TIME = 4000L
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        setListener()
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private fun showActivity(packageName: String, activityDir: String) {
        val intent = Intent()
        intent.component = ComponentName(packageName, activityDir)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun isIgnoringBatteryOptimizations(): Boolean {
        var isIgnoring = false
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName)
        return isIgnoring
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestIgnoreBatteryOptimizations() {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initData() {
        App.mEndDate.observe(this, Observer {
            tvLastDate.text = it
        })
        App.mLastTimer.observe(this, Observer<String> {
            tvLastTimer.text = it
        })
        App.mTimer.observe(this, Observer<String> {
            tvTimer.text = it
        })
        App.mStatus.observe(this, Observer {
            tvStatus.text = if (it) {
                "Operating status(运行状态):Running(运行中)"
            } else {
                "Operating status(运行状态):Stopped(已停止)"
            }
        })
    }

    private fun setListener() {
        //更新通知栏信息
//        btnUpdate.onClick {
//            val num = (0..8).random()
//            cactusUpdateNotification {
//                setTitle(list[num].first)
//                setContent(list[num].second)
//            }
//        }
        //停止
        btnStop.onClick {
            cactusUnregister()
        }
        //停止
        btnStop.onClick {
            cactusUnregister()
        }

        //申请忽略电池优化权限
        btnRestart.onClick {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (!isIgnoringBatteryOptimizations()){
                    requestIgnoreBatteryOptimizations()
                }
            }
        }

        //申请自启动权限
        btnCrash.setOnClickListener {
//            showActivity(
//                    "com.miui.securitycenter",
//                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
//            )

            val intent = getAutostartSettingIntent(this)
            startActivity(intent)
        }
    }

    private inline fun View.onClick(crossinline block: () -> Unit) {
        setOnClickListener {
//            val nowTime = System.currentTimeMillis()
//            val intervals = nowTime - times
//            if (intervals > TIME) {
//                times = nowTime
//                block()
//            } else {
//                Toast.makeText(
//                    context,
//                    ((TIME.toFloat() - intervals) / 1000).toString() + "秒之后再点击",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }
    }

    /**
     * 获取自启动管理页面的Intent
     * @param context context
     * @return 返回自启动管理页面的Intent
     */
    fun getAutostartSettingIntent(context: Context): Intent? {
        var componentName: ComponentName? = null
        val brand = Build.MANUFACTURER
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        when (brand.toLowerCase()) {
            "samsung" -> componentName = ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity")
            "huawei" ->             //荣耀V8，EMUI 8.0.0，Android 8.0上，以下两者效果一样
                componentName = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")
            "xiaomi" -> componentName = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            "vivo" -> //            componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.safaguard.PurviewTabActivity");
                componentName = ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")
            "oppo" -> //            componentName = new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                componentName = ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")
            "yulong", "360" -> componentName = ComponentName("com.yulong.android.coolsafe", "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity")
            "meizu" -> componentName = ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity")
            "oneplus" -> componentName = ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity")
            "letv" -> {
                intent.action = "com.letv.android.permissionautoboot"
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
            }
            else -> {
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
            }
        }
        intent.component = componentName
        return intent
    }
}