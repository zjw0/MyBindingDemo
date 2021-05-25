package com.tb.running.mydemo

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.protobuf.StringValue
import com.bumptech.glide.Glide
import com.tb.running.mydemo.base.BaseActivity
import com.tb.running.mydemo.databinding.ActivityMainBinding
import com.tb.running.mydemo.ui.bean.InviteActiveBean
import com.tb.running.mydemo.utils.GlobalUtil
import com.tb.running.mydemo.utils.logD
import com.tb.running.mydemo.utils.setOnClickListener

class MainActivity : BaseActivity() {

    var _binding: ActivityMainBinding? = null

    val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setupViews() {
        initView()
    }
    private fun initView() {
        setOnClickListener(binding.llViewVer) {
                when (this) {
                    binding.llViewVer ->{
                        Toast.makeText(context,"进度条",Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //模拟数据
        var list = mutableListOf<InviteActiveBean.RSPDATA.ACTPROS>()
        for(i in 0..4){
            var number = (i*2+1).toString()
            list.add(i,InviteActiveBean.RSPDATA.ACTPROS("",number,false))
        }
        var data = InviteActiveBean.RSPDATA(list)
        var datas = InviteActiveBean("",data,"")

        initActiveData(datas)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * 动态添加进度条
     */
    private fun initActiveData(bean: InviteActiveBean) {
        try {
            val sum = bean.rSPDATA.aCTPROS.size
            //动态添加view
            binding.llViewHorImg.removeAllViews() //活动图片
            binding.flViewProgress.removeAllViews() //活动图片
            binding.rlViewHorText.removeAllViews() //clear linearlayout
            for (i in 0 until sum) {
                if (i == (sum - 1)) {
                    //动态添加图片
                    val imageView = ImageView(this)
                    val layoutImg = LinearLayout.LayoutParams(
                        GlobalUtil.dip2px(this, 130f), GlobalUtil.dip2px(this, 65f)
                    )
                    layoutImg.setMargins(
                        GlobalUtil.dip2px(this, 15f),
                        0,
                        GlobalUtil.dip2px(this, 15f),
                        0
                    )
                    imageView.layoutParams = layoutImg
                    imageView.scaleType = ImageView.ScaleType.FIT_XY
                    binding.llViewHorImg.addView(imageView)
                    Glide.with(this)
                        .load(GlobalUtil.object2String(bean.rSPDATA.aCTPROS[i].iNVITEIMAGE))
                        .error(R.mipmap.ic_launcher)
                        .into(imageView)
                } else {
                    //动态添加图片
                    val imageView = ImageView(this)
                    val layoutImg = LinearLayout.LayoutParams(
                        GlobalUtil.dip2px(this, 130f), GlobalUtil.dip2px(this, 65f)
                    )
                    layoutImg.setMargins(GlobalUtil.dip2px(this, 15f), 0, 0, 0)
                    imageView.layoutParams = layoutImg
                    imageView.scaleType = ImageView.ScaleType.FIT_XY
                    binding.llViewHorImg.addView(imageView)
                    Glide.with(this)
                        .load(GlobalUtil.object2String(bean.rSPDATA.aCTPROS[i].iNVITEIMAGE))
                        .error(R.mipmap.ic_launcher)
                        .into(imageView)
                }
                //设置默认进度条结点为灰色
                bean.rSPDATA.aCTPROS[i].isOut = false
            }

            //获取活动商品图片总宽度
            binding.llViewHorImg.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= 16) {
                            binding.llViewHorImg.viewTreeObserver
                                .removeOnGlobalLayoutListener(this)
                        } else {
                            binding.llViewHorImg.viewTreeObserver
                                .removeGlobalOnLayoutListener(this)
                        }
                        //图片布局总宽度
                        var widthAll = binding.llViewHorImg.width
                        //图片间距px
                        var marginWidth = GlobalUtil.dip2px(this@MainActivity, 15f)
                        //进度条节点左移距离px
                        var marginLeftWidth = GlobalUtil.dip2px(this@MainActivity, 10f)
                        //半张图片px
                        var imageHelf = (widthAll - marginWidth * (sum + 1)) / (sum * 2)
                        //进度条总宽度px
                        var progressWidth = sum * marginWidth + imageHelf * (1 + (sum - 1) * 2) - marginLeftWidth
                        var progressOutWidth = 0
                        try {
                            //进度条已达标百分比宽度px
                            var outSum = bean.rSPDATA.bNUM.toInt()

                            for (i in 0 until sum) {
                                if (outSum >= bean.rSPDATA.aCTPROS[i].iNVITENUMBER.toInt()) {
                                    progressOutWidth = (i + 1) * marginWidth + imageHelf * (1 + i * 2) - marginLeftWidth
                                    bean.rSPDATA.aCTPROS[i].isOut = true
                                    logD("进度条宽度设置比例", "是" + progressOutWidth)
                                } else {
                                    bean.rSPDATA.aCTPROS[i].isOut = false
                                    var beforeIndex = i - 1
                                    var afterIndex = i + 1
                                    if (beforeIndex < 0) {
                                        beforeIndex = 0
                                    }
                                    if (afterIndex >= sum) {
                                        afterIndex = sum - 1
                                    }
                                    if (outSum > 0 && outSum < bean.rSPDATA.aCTPROS[i].iNVITENUMBER.toInt()
                                        && outSum < bean.rSPDATA.aCTPROS[afterIndex].iNVITENUMBER.toInt()
                                    ) {
                                        logD("进度条宽度设置比例2", "是" + progressOutWidth)
                                        if (outSum == bean.rSPDATA.aCTPROS[beforeIndex].iNVITENUMBER.toInt()) {
                                            progressOutWidth = (beforeIndex + 1) * marginWidth + imageHelf * (1 + beforeIndex * 2) - marginLeftWidth
                                        } else {
                                            progressOutWidth = (((i + 1) * marginWidth + imageHelf * (1 + i * 2) - marginLeftWidth) / (bean.rSPDATA.aCTPROS[i].iNVITENUMBER.toInt())) * outSum
                                            logD("进度条宽度设置比例3", "是" + progressOutWidth)
                                        }
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        logD("进度条图片布局宽度", "是" + widthAll)
                        logD("进度条图片间距", "是" + marginWidth)
                        logD("进度条半张图片", "是" + imageHelf)
                        logD("进度条总宽度", "是" + progressWidth)
                        if (sum > 0) {
                            //动态添加进度条
                            val textView = TextView(this@MainActivity)
                            logD("进度条宽度", "是" + widthAll.toFloat())
                            val layoutProgress = FrameLayout.LayoutParams(
                                progressWidth, GlobalUtil.dip2px(this@MainActivity, 10f)
                            )
                            layoutProgress.setMargins(
                                GlobalUtil.dip2px(this@MainActivity, 15f),
                                GlobalUtil.dip2px(this@MainActivity, 5f),
                                0,
                                0
                            )
                            textView.layoutParams = layoutProgress
                            textView.setBackgroundResource(R.drawable.shape_solid_gray_10dp)
                            binding.flViewProgress.addView(textView)
                            //动态添加已达标进度条颜色
                            val textViewOut = TextView(this@MainActivity)
                            val layoutOutProgress = FrameLayout.LayoutParams(
                                progressOutWidth, GlobalUtil.dip2px(this@MainActivity, 10f)
                            )
                            layoutOutProgress.setMargins(
                                GlobalUtil.dip2px(this@MainActivity, 15f),
                                GlobalUtil.dip2px(this@MainActivity, 5f),
                                0,
                                0
                            )
                            textViewOut.layoutParams = layoutOutProgress
                            textViewOut.setBackgroundResource(R.drawable.shape_solid_green_10dp)
                            binding.flViewProgress.addView(textViewOut)
                            //动态添加邀请人数父容器宽度
                            val textSumView = TextView(this@MainActivity)
                            val layoutTextBg = RelativeLayout.LayoutParams(
                                progressWidth, GlobalUtil.dip2px(this@MainActivity, 20f)
                            )
                            textSumView.layoutParams = layoutProgress
                            textSumView.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.white
                                )
                            )
                            binding.rlViewHorText.addView(textSumView)
                        }
                        for (i in 0 until sum) {
                            //动态添加图片
                            val imageBg = ImageView(this@MainActivity)
                            var leftMargin = (i + 1) * marginWidth + imageHelf * (1 + i * 2) - marginLeftWidth
                            logD("进度条节点宽度", "是" + leftMargin)
                            val layoutBg = FrameLayout.LayoutParams(
                                GlobalUtil.dip2px(this@MainActivity, 20f),
                                GlobalUtil.dip2px(this@MainActivity, 20f)
                            )
                            layoutBg.setMargins(leftMargin, 0, 0, 0)
                            imageBg.layoutParams = layoutBg
                            //设置达标节点变色
                            if (bean.rSPDATA.aCTPROS[i].isOut) {
                                imageBg.setImageResource(R.mipmap.label_icon_d)
                            } else {
                                imageBg.setImageResource(R.mipmap.label_icon2_d)
                            }
                            binding.flViewProgress.addView(imageBg)
                            //动态添加人数
                            val textInviteSumView = TextView(this@MainActivity)
                            var leftSumMargin = (i + 1) * marginWidth + imageHelf * (1 + i * 2) - GlobalUtil.dip2px(this@MainActivity, 11f)
                            logD("进度条人数节点宽度", "是" + leftSumMargin)
                            val layoutTextBg = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                GlobalUtil.dip2px(this@MainActivity, 20f)
                            )
                            layoutTextBg.setMargins(leftSumMargin, 0, 0, 0)
                            textInviteSumView.layoutParams = layoutTextBg
                            textInviteSumView.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.black
                                )
                            )
                            textInviteSumView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
                            textInviteSumView.setText(bean.rSPDATA.aCTPROS[i].iNVITENUMBER + "个")
                            binding.rlViewHorText.addView(textInviteSumView)
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}