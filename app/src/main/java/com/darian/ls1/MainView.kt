package com.darian.ls1

import android.content.ClipData
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.darian.ls1.Model.ApiResponse
import com.darian.ls1.Model.LinkCreateResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MainView : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //mine
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var shortLinkTxt: TextView
    private lateinit var mainActivity: MainActivity
    private lateinit var mainView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_main_view, container, false)


        if (v === null) throw Exception("Error")
        mainActivity = (activity as MainActivity)
        mainView = v as View


        //send link
        val btn_confirm = mainView.findViewById<Button>(R.id.btn_confirm)
        btn_confirm.setOnClickListener {
            sendLink()
        }


        //copy link
        shortLinkTxt = mainView.findViewById<TextView>(R.id.txt_shortLink)
        shortLinkTxt.setOnClickListener {
            copyLink()
        }


        //enter button
        textInputLayout = mainView.findViewById<TextInputLayout>(R.id.txt_input_link)
        val textInputEditText = mainView.findViewById<TextInputEditText>(R.id.editText)
        textInputEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == 66) {
                sendLink()
                return@OnKeyListener true
            }
            false
        })


        // Inflate the layout for this fragment
        return v
    }


    private fun copyLink() {
        val link = shortLinkTxt.text
        if (link == null || link == "") return
        val clipboard = mainActivity.getClipboard()
        val clip = ClipData.newPlainText(getString(R.string.linkTxt), link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(mainActivity, getString(R.string.linkCopied), Toast.LENGTH_SHORT)
            .show()
    }

    private fun sendLink() {
        if (mainActivity.btn_confirm_check) return
        val btn_confirm = mainView.findViewById<Button>(R.id.btn_confirm)
        textInputLayout.clearFocus()
        val linkTxt = textInputLayout.editText?.text.toString()
        mainActivity.startLoading(btn_confirm)
        mainActivity.closeKeyBoard()
        callCreateLink(linkTxt, btn_confirm)
    }


    private fun callCreateLink(linkTxt: String, btn: Button) {
        mainActivity.linkService.createLink(linkTxt)
            .enqueue(object : Callback<ApiResponse<LinkCreateResponse>> {
                /* The HTTP call failed. This method is run on the main thread */
                override fun onFailure(call: Call<ApiResponse<LinkCreateResponse>>, t: Throwable) {
                    createLinkFailure(call, t)
                    mainActivity.stopLoading(btn)
                }

                /* The HTTP call was successful, we should still check status code and response body
                 * on a production app. This method is run on the main thread */
                override fun onResponse(
                    call: Call<ApiResponse<LinkCreateResponse>>,
                    response: Response<ApiResponse<LinkCreateResponse>>
                ) {
                    createLinkResponse(call, response)
                    mainActivity.stopLoading(btn)
                }
            })
    }

    private fun createLinkFailure(call: Call<ApiResponse<LinkCreateResponse>>, t: Throwable) {
        t.printStackTrace()
        Toast.makeText(mainActivity, getString(R.string.problem), Toast.LENGTH_SHORT)
            .show()
    }

    private fun createLinkResponse(
        call: Call<ApiResponse<LinkCreateResponse>>,
        response: Response<ApiResponse<LinkCreateResponse>>
    ) {
        val shortLink = response.body()?.data?.shortLink;
        if (response.body() == null) {
            Toast.makeText(
                mainActivity,
                getString(R.string.problem),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (response.body()?.ok != true) {
            Toast.makeText(
                mainActivity,
                response.body()?.error,
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        if (shortLink == null) {
            Toast.makeText(
                mainActivity,
                getString(R.string.problem),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        shortLinkTxt.text = mainActivity.baseUrl + "/" + response.body()?.data?.shortLink
        Toast.makeText(
            mainActivity,
            getString(R.string.link_ready),
            Toast.LENGTH_SHORT
        ).show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}