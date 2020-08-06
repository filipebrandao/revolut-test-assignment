package com.filipebrandao.revolutassignment.ui.rates

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.filipebrandao.revolutassignment.R
import com.filipebrandao.revolutassignment.ui.BaseFragment
import com.filipebrandao.revolutassignment.ui.rates.models.RateView
import com.filipebrandao.revolutassignment.ui.rates.models.RatesResult
import com.filipebrandao.revolutassignment.utils.OnScrollStoppedListener
import com.filipebrandao.revolutassignment.utils.bindView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

class RatesFragment : BaseFragment() {

    companion object {
        private const val TAG = "RatesFragment"
        private const val SAVED_INSTANCE_KEY_RATES = "KEY_RATES"
        private const val SAVED_INSTANCE_KEY_ACTIVE_CURRENCY = "SAVED_INSTANCE_KEY_ACTIVE_CURRENCY"
        fun newInstance() = RatesFragment()
    }

    private val viewModel: RatesViewModel by viewModel()

    private val recyclerView by bindView<RecyclerView>(R.id.list)
    private val loadingGroup by bindView<Group>(R.id.loadingGroup)
    private val errorGroup by bindView<Group>(R.id.errorGroup)
    private val retryButton by bindView<Button>(R.id.retryButton)

    /**
     * Focuses on the recyclerView once it stops scrolling. Helpful after scrolling the list to the top when a new currency is selected.
     */
    private val focusFirstEditTextOnScrollStoppedListener = object : OnScrollStoppedListener() {
        override fun onScrollStopped(recyclerView: RecyclerView) {
            recyclerView.removeOnScrollListener(this)
            // focusing the recyclerview will focus the first EditText  since it is its first focusable view
            recyclerView.requestFocus()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.rates_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        savedInstanceState?.let {
            restoreSavedState(savedInstanceState)
        }
        setupObservers()
    }

    /**
     * Restores
     */
    private fun restoreSavedState(savedInstanceState: Bundle) {
        val activeCurrencyValue = savedInstanceState.getSerializable(SAVED_INSTANCE_KEY_ACTIVE_CURRENCY) as BigDecimal?
        val rates = savedInstanceState.getParcelableArrayList<RateView>(SAVED_INSTANCE_KEY_RATES)
        if (activeCurrencyValue != null && rates != null) {
            logger.d(TAG, "Restoring saved state")
            setData(rates)
            (recyclerView.adapter as RatesListAdapter).setActiveCurrencyValue(activeCurrencyValue)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (recyclerView.adapter as RatesListAdapter?)?.let {
            // save the recyclerview adapter state
            outState.putSerializable(SAVED_INSTANCE_KEY_ACTIVE_CURRENCY, it.getActiveCurrencyValue())
            outState.putParcelableArrayList(SAVED_INSTANCE_KEY_RATES, it.getRates())
        }
    }

    private fun setupUi() {
        recyclerView.apply {
            setHasFixedSize(true)

            layoutManager = object : LinearLayoutManager(context) {
                override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect, immediate: Boolean, focusedChildVisible: Boolean): Boolean {
                    // prevents the recyclerview from scrolling automatically , due to the EditText focus, when it's scrolled down and back up while the softkeyboard is showing
                    return false
                }
            }

            // disable blinking animation when the rates are updated
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun setupObservers() {
        viewModel.rates.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is RatesResult.Success -> {
                        logger.d(TAG, "Got %d updated rates", it.rates.size)
                        handleRates(it.rates)
                    }
                    is RatesResult.Error -> {
                        showError()
                    }
                }
            }
        )

        viewModel.observeRates()
    }

    private fun handleRates(rates: List<RateView>) {
        if (recyclerView.adapter == null) {
            logger.d(TAG, "Show the list for the first time")
            setData(rates)
        } else {
            logger.d(TAG, "Updating the currencies list adapter")
            (recyclerView.adapter as RatesListAdapter).updateRates(rates)
        }
    }

    private fun setData(rates: List<RateView>) {
        loadingGroup.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = RatesListAdapter(activity!!, ArrayList(rates), ::onListItemSelected)
    }

    /**
     * Scrolls the top of the recyclerView once a currency is selected then focuses on the first EditText to show the softkeyboard
     */
    private fun onListItemSelected() {
        recyclerView.removeOnScrollListener(focusFirstEditTextOnScrollStoppedListener)
        recyclerView.addOnScrollListener(focusFirstEditTextOnScrollStoppedListener)
        recyclerView.scrollToPosition(0)
    }

    private fun showError() {
        logger.e(TAG, "Got an error while fetching the rates")

        if (recyclerView.visibility == View.VISIBLE) {
            // show the error screen only if the list is not being shown already
            // otherwise just let the list be visible and automatically retry the rate updates
            viewModel.observeRates()
            return
        }

        loadingGroup.visibility = View.GONE
        errorGroup.visibility = View.VISIBLE

        retryButton.setOnClickListener {
            loadingGroup.visibility = View.VISIBLE
            errorGroup.visibility = View.GONE
            viewModel.observeRates()
        }
    }
}
