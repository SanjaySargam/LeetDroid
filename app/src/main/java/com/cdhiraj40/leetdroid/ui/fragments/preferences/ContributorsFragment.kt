package com.cdhiraj40.leetdroid.ui.fragments.preferences

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cdhiraj40.leetdroid.R
import com.cdhiraj40.leetdroid.adapter.ContributorListAdapter
import com.cdhiraj40.leetdroid.api.GithubApi
import com.cdhiraj40.leetdroid.databinding.FragmentContributorsBinding
import com.cdhiraj40.leetdroid.model.ContributorListModel
import com.cdhiraj40.leetdroid.utils.CommonUtils.openLink
import com.cdhiraj40.leetdroid.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContributorsFragment : Fragment(), ContributorListAdapter.ContributorClickInterface {

    private lateinit var contributorsBinding: FragmentContributorsBinding
    private lateinit var contributorListAdapter: ContributorListAdapter
    private lateinit var loadingView: View
    private lateinit var errorLoadingView:View;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        contributorsBinding = FragmentContributorsBinding.inflate(inflater)
        val root_view = contributorsBinding.root

        loadingView = root_view.findViewById(R.id.loading_view)
        errorLoadingView=root_view.findViewById(R.id.view_general_error)
        loadingView.visibility = View.VISIBLE
        contributorsBinding.contributorRecyclerView.visibility=View.GONE;

        val rootView = contributorsBinding.root

        getContributors()
        return rootView
    }

    private fun getContributors(
    ) {
        val apiInterface =
            GithubApi.create().getContributors(Constant().userName, Constant().repositoryName)

        apiInterface.enqueue(object : Callback<ContributorListModel> {
            override fun onResponse(
                Call: Call<ContributorListModel>?,
                response: Response<ContributorListModel>?
            ) {
                if (response?.body() != null) {
                    val body = response.body()!!
                    Log.d(
                        Constant.TAG(ContributorsFragment::class.java).toString(), body.toString()
                    )
                    setUpRecyclerView(body)
                    loadingView.visibility = View.GONE
                    contributorsBinding.contributorRecyclerView.visibility=View.VISIBLE;
                }
            }

            override fun onFailure(call: Call<ContributorListModel>?, throwable: Throwable) {
                Log.d(
                    Constant.TAG(ContributorsFragment::class.java).toString(),
                    throwable.message,
                    throwable
                )
                errorLoadingView.visibility=View.VISIBLE;
                loadingView.visibility = View.GONE
            }
        })
    }

    private fun setUpRecyclerView(body: ContributorListModel) {
        contributorListAdapter = ContributorListAdapter(requireContext(), this, body)

        contributorsBinding.contributorRecyclerView.layoutManager =
            LinearLayoutManager(context)
        contributorsBinding.contributorRecyclerView.adapter =
            contributorListAdapter
    }

    override fun onContributorClick(contributor: ContributorListModel.ContributorListModelItem) {
        openLink(requireContext(), contributor.author?.html_url.toString())
    }
}