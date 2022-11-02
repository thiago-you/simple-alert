package you.thiago.simplealert

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.text.HtmlCompat
import com.google.android.material.internal.ContextUtils.getActivity
import you.thiago.simplealert.databinding.AlertBinding
import you.thiago.simplealert.easy.EasyAlert

class InternalSimpleAlert constructor(private val viewGroup: ViewGroup) : View.OnClickListener {

    private lateinit var backgroundBlur: ImageView
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var binding: AlertBinding

    private var confirmClickListener: OnSimpleAlertClickListener? = null
    private var cancelClickListener: OnSimpleAlertClickListener? = null

    private var messageRaw: String = ""
    private var customViewState: Boolean? = null

    interface OnSimpleAlertClickListener {
        fun onClick(internalSimpleAlert: InternalSimpleAlert)
    }

    init {
        enableDisableView(viewGroup.rootView, false)
        setupComponent()
    }

    @SuppressLint("RestrictedApi")
    private fun setupComponent() {
        relativeLayout = RelativeLayout(viewGroup.context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        backgroundBlur = ImageView(viewGroup.context).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0.65F
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val inflater: LayoutInflater = getActivity(viewGroup.context)?.layoutInflater ?: throw Exception(viewGroup.context.getString(R.string.alert_inflater_error))

        binding = AlertBinding.inflate(inflater).apply {
            btnShowExtras.visibility = View.GONE
            btnCancel.visibility = View.GONE
            imgIcon.visibility = View.GONE

            btnConfirm.setOnClickListener(this@InternalSimpleAlert)
            btnShowExtras.setOnClickListener(this@InternalSimpleAlert)
        }

        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        binding.root.layoutParams = layoutParams
    }

    fun setTitle(title: String): InternalSimpleAlert {
        runOnUi { binding.txtTitle.text = title }
        return this
    }

    fun setMessage(message: String): InternalSimpleAlert {
        messageRaw = message

        runOnUi {
            if (message.length > 500) {
                binding.txtMessage.text = viewGroup.context.getString(R.string.long_message, message.take(500))

                binding.btnShowExtras.visibility = View.VISIBLE
                binding.btnShowExtras.text = HtmlCompat.fromHtml(viewGroup.context.getString(R.string.fmt_btn_show_message), HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                binding.txtMessage.text = message
            }
        }

        return this
    }

    fun setMessage(messageRes: Int): InternalSimpleAlert {
        val message = viewGroup.context.getString(messageRes)

        messageRaw = message

        runOnUi {
            if (message.length > 500) {
                binding.txtMessage.text = viewGroup.context.getString(R.string.long_message, message.take(500))

                binding.btnShowExtras.visibility = View.VISIBLE
                binding.btnShowExtras.text = HtmlCompat.fromHtml(viewGroup.context.getString(R.string.fmt_btn_show_message), HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                binding.txtMessage.text = message
            }
        }

        return this
    }

    fun setBtnConfirmTitle(btnConfirmTitle: String): InternalSimpleAlert {
        runOnUi {
            binding.btnConfirm.text = btnConfirmTitle
            binding.btnConfirm.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        }

        return this
    }

    fun setBtnCancelTitle(btnCancelTitle: String): InternalSimpleAlert {
        runOnUi {
            binding.btnCancel.text = btnCancelTitle
            binding.btnCancel.visibility = View.VISIBLE
        }

        return this
    }

    fun setConfirmListener(confirmListener: Common.OnClickListener): InternalSimpleAlert {
        return setConfirmClickListener(object : OnSimpleAlertClickListener {
            override fun onClick(internalSimpleAlert: InternalSimpleAlert) {
                confirmListener.onClick()

                enableDisableView(viewGroup.rootView, true)

                viewGroup.removeView(backgroundBlur)
                viewGroup.removeView(relativeLayout)
            }
        })
    }

    fun setCancelListener(cancelListener: Common.OnClickListener): InternalSimpleAlert {
        runOnUi {
            binding.btnCancel.visibility = View.VISIBLE
        }

        return setCancelClickListener(object : OnSimpleAlertClickListener {
            override fun onClick(internalSimpleAlert: InternalSimpleAlert) {
                cancelListener.onClick()

                enableDisableView(viewGroup.rootView, true)

                viewGroup.removeView(backgroundBlur)
                viewGroup.removeView(relativeLayout)
            }
        })
    }

    fun setViewStateEnabled(state: Boolean) {
        customViewState = state
        enableDisableView(viewGroup.rootView, state)
    }

    fun enableAlertInterface() {
        toggleInterfaceState(true)
    }

    fun disableAlertInterface() {
        toggleInterfaceState(false)
    }

    fun toggleInterfaceState(state: Boolean) {
        runOnUi {
            binding.btnCancel.isEnabled = state
            binding.btnConfirm.isEnabled = state
        }
    }

    private fun setConfirmClickListener(confirmClickListener: OnSimpleAlertClickListener): InternalSimpleAlert {
        this.confirmClickListener = confirmClickListener
        return this
    }

    private fun setCancelClickListener(cancelClickListener: OnSimpleAlertClickListener): InternalSimpleAlert {
        this.confirmClickListener = cancelClickListener
        return this
    }

    fun show() {
        relativeLayout.addView(binding.root)
        viewGroup.addView(backgroundBlur)

        runOnUi {
            viewGroup.addView(relativeLayout)
        }
    }

    override fun onClick(v: View?) {
        v?.also { button ->
            runOnUi {
                when (button.id) {
                    binding.btnConfirm.id -> {
                        if (confirmClickListener != null) {
                            confirmClickListener?.onClick(this)
                            enableDisableView(viewGroup.rootView, true)
                        } else {
                            viewGroup.removeView(backgroundBlur)
                            viewGroup.removeView(relativeLayout)
                        }
                    }
                    binding.btnCancel.id -> {
                        if (cancelClickListener != null) {
                            cancelClickListener?.onClick(this)
                            enableDisableView(viewGroup.rootView, true)
                        } else {
                            viewGroup.removeView(backgroundBlur)
                            viewGroup.removeView(relativeLayout)
                        }
                    }
                    binding.btnShowExtras.id -> {
                        if (messageRaw.isNotEmpty()) {
                            EasyAlert.Default(viewGroup.context).build(viewGroup.context.getString(R.string.message), messageRaw)
                                    .setBtnConfirmTitle(R.string.btn_close)
                                    .show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Enable/Disable deep ui dialog clicks
     */
    private fun enableDisableView(view: View, enabled: Boolean) {
        view.isEnabled = customViewState ?: enabled

        if (view is ViewGroup) {
            for (idChild in 0 until view.childCount) {
                enableDisableView(view.getChildAt(idChild), customViewState ?: enabled)
            }
        }
    }

    /**
     * Run code action on UI thread
     */
    private fun runOnUi(action: Runnable) {
        if (viewGroup.context is Activity) {
            (viewGroup.context as Activity).runOnUiThread(action)
        } else {
            Handler(Looper.getMainLooper()).post(action)
        }
    }
}