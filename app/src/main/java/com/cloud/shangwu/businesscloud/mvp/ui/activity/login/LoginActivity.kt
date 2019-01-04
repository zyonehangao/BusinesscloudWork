package com.cloud.shangwu.businesscloud.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import com.cloud.shangwu.businesscloud.R
import com.cloud.shangwu.businesscloud.base.BaseActivity
import com.cloud.shangwu.businesscloud.constant.Constant
import com.cloud.shangwu.businesscloud.event.LoginEvent
import com.cloud.shangwu.businesscloud.event.MessageEvent
import com.cloud.shangwu.businesscloud.ext.showToast
import com.cloud.shangwu.businesscloud.im.ui.activity.DialogsActivity
import com.cloud.shangwu.businesscloud.im.utils.chat.ChatHelper
import com.cloud.shangwu.businesscloud.mvp.contract.LoginContract
import com.cloud.shangwu.businesscloud.mvp.model.bean.LoginData
import com.cloud.shangwu.businesscloud.mvp.presenter.LoginPresenter
import com.cloud.shangwu.businesscloud.mvp.ui.activity.login.ForgetPassword
import com.cloud.shangwu.businesscloud.mvp.ui.activity.login.MainActivity
import com.cloud.shangwu.businesscloud.mvp.ui.activity.login.RegisterActivity
import com.cloud.shangwu.businesscloud.utils.DialogUtil
import com.cloud.shangwu.businesscloud.utils.JumpUtil
import com.cloud.shangwu.businesscloud.utils.Preference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.white_toolbar.*
import org.greenrobot.eventbus.EventBus
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.model.QBUser
import com.quickblox.core.QBEntityCallback
import io.reactivex.Single


class LoginActivity : BaseActivity(), LoginContract.View {

    /**
     * local username
     */
    private var user: String by Preference(Constant.USERNAME_KEY, "")

    /**
     * local password
     */
    private var pwd: String by Preference(Constant.PASSWORD_KEY, "")


    /**
     * token
     */
    private var token: String by Preference(Constant.TOKEN_KEY, "")

    private val mPresenter: LoginPresenter by lazy {
        LoginPresenter()
    }

    private val mDialog by lazy {
        DialogUtil.getWaitDialog(this, getString(R.string.login_ing))
    }

    override fun showLoading() {
        mDialog.show()
    }

    override fun hideLoading() {
        mDialog.dismiss()
    }

    override fun showError(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun attachLayoutRes(): Int = R.layout.activity_login

    override fun useEventBus(): Boolean = true

    override fun enableNetworkTip(): Boolean = false

    override fun initData() {
    }

    override fun initView() {

        tl_title.run {
            title = ""
            toolbar_withe_name.run {
                text = getString(R.string.login)

            }
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        mPresenter.attachView(this)
        et_username.setText(user)
        btn_login.setOnClickListener(onClickListener)
        tv_sign_up.setOnClickListener(onClickListener)
        tv_forgetpsd.setOnClickListener(onClickListener)
    }

    override fun start() {

    }

    override fun loginSuccess(data: LoginData) {
        showToast(getString(R.string.login_success))
        isLogin = true
        user = data.username
        pwd = et_password.text.toString()
        token = data.token

        EventBus.getDefault().postSticky(LoginEvent(isLogin, data))

        var bundle = Bundle()
        bundle.putSerializable("login", data)

        JumpUtil.Next(this, MainActivity::class.java, bundle)
        finish()
    }


    override fun loginFail() {
        hideLoading()
    }

    /**
     * OnClickListener
     */
    @RequiresApi(Build.VERSION_CODES.ECLAIR)
    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_login -> {
                login()
            }
            R.id.tv_sign_up -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)

            }
            R.id.tv_forgetpsd -> {
                val intent = Intent(this@LoginActivity, ForgetPassword::class.java)
                startActivity(intent)
            }
        }
    }

    /**
     * Login
     */
    private fun login() {

        if (validate()) run {
            //            mPresenter.login(et_username.text.toString(), et_password.text.toString(), invitation_code.text.toString())
//            val user = QBUser("fang", "12345678")
//            ChatHelper.getInstance().login(user, object : QBEntityCallback<Void?> {
//                override fun onError(p0: QBResponseException?) {
//                }
//
//                override fun onSuccess(p0: Void?, p1: Bundle?) {
//
//                }
//            })
            mPresenter.combineLogin(et_username.text.toString(), et_password.text.toString(), invitation_code.text.toString())
        }

    }

    /**
     * Check UserName and PassWord
     */
    private fun validate(): Boolean {
        var valid = true
        val username: String = et_username.text.toString()
        val password: String = et_password.text.toString()

        if (username.isEmpty()) {
            et_username.error = getString(R.string.username_not_empty)
            valid = false
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.password_not_empty)
            valid = false
        }
        return valid

    }

    override fun onDestroy() {
        mDialog.dismiss()
        super.onDestroy()
    }

}
