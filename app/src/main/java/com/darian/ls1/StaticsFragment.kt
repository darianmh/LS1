package com.darian.ls1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.darian.ls1.Model.ApiResponse
import com.darian.ls1.Model.ShowStaticsResponse
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

/**
 * A simple [Fragment] subclass.
 * Use the [StaticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StaticsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //mine
    private lateinit var mainActivity: MainActivity
    private lateinit var mainView: View
    private lateinit var staticsTextInputLayout: TextInputLayout

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
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_statics, container, false)
        if (v === null) throw Exception("Error")
        mainView = v as View
        mainActivity = activity as MainActivity

        //statics enter button
        staticsTextInputLayout = mainView.findViewById<TextInputLayout>(R.id.static_Holder_txt)
        var staticsTextInputEditText = mainView.findViewById<TextInputEditText>(R.id.static_txt)
        staticsTextInputEditText.setOnKeyListener(View.OnKeyListener { vv, keyCode, event ->
            if (keyCode == 66) {
                showStatics()
                return@OnKeyListener true
            }
            false
        })


        //show statics
        var btn_show_statics = mainView.findViewById<Button>(R.id.btn_show_statics)
        btn_show_statics.setOnClickListener {
            showStatics()
        }

        return v
    }


    private fun callShowStatics(linkTxt: String, btn: Button) {
        mainActivity.linkService.showStatics(linkTxt)
            .enqueue(object : Callback<ApiResponse<ShowStaticsResponse>> {
                /* The HTTP call failed. This method is run on the main thread */
                override fun onFailure(call: Call<ApiResponse<ShowStaticsResponse>>, t: Throwable) {
                    createStaticsFailure(call, t)
                    mainActivity.stopLoading(btn)
                }

                /* The HTTP call was successful, we should still check status code and response body
                 * on a production app. This method is run on the main thread */
                override fun onResponse(
                    call: Call<ApiResponse<ShowStaticsResponse>>,
                    response: Response<ApiResponse<ShowStaticsResponse>>
                ) {
                    createStaticsResponse(call, response)
                    mainActivity.stopLoading(btn)
                }
            })
    }

    private fun showStatics() {
        if (mainActivity.btn_confirm_check) return
        var btn_show_statics = mainView.findViewById<Button>(R.id.btn_show_statics)
        staticsTextInputLayout.clearFocus()
        var linkTxt = staticsTextInputLayout.editText?.text.toString()
        mainActivity.startLoading(btn_show_statics)
        mainActivity.closeKeyBoard()
        callShowStatics(linkTxt, btn_show_statics)
    }

    private fun createStaticsFailure(call: Call<ApiResponse<ShowStaticsResponse>>, t: Throwable) {
        t.printStackTrace()
        Toast.makeText(mainActivity, getString(R.string.problem), Toast.LENGTH_SHORT)
            .show()
    }

    private fun createStaticsResponse(
        call: Call<ApiResponse<ShowStaticsResponse>>,
        response: Response<ApiResponse<ShowStaticsResponse>>
    ) {
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
        var shortLink = response.body()?.data?.link?.shortLink;
        if (shortLink == null) {
            Toast.makeText(
                mainActivity,
                getString(R.string.not_found),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        var sl = mainView.findViewById<TextView>(R.id.txt_show_shortLink)
        sl.text = shortLink
        var ml = mainView.findViewById<TextView>(R.id.txt_show_mainLink)
        ml.text = response.body()?.data?.link?.mainLink
        var c = mainView.findViewById<TextView>(R.id.txt_show_staticsCount)
        c.text = response.body()?.data?.statics?.count().toString()

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StaticsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaticsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}