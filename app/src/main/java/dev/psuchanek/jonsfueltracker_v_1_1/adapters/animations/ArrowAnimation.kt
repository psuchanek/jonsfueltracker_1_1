package dev.psuchanek.jonsfueltracker_v_1_1.adapters.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import dev.psuchanek.jonsfueltracker_v_1_1.adapters.TripHistoryAdapter
import dev.psuchanek.jonsfueltracker_v_1_1.utils.ARROW_ANIM_DOWN
import dev.psuchanek.jonsfueltracker_v_1_1.utils.ARROW_ANIM_UP

class ArrowAnimation() : DefaultItemAnimator() {
    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder) = true

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) = true


    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        val holder = newHolder as TripHistoryAdapter.ViewHolder

        if (preInfo is TripItemHolderInfo) {
            if (preInfo.isExpanded) {
                val animator = ObjectAnimator.ofFloat(holder.binding.ivArrow, View.ROTATION, 180f)
                animator.addListener(object :AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        dispatchAnimationFinished(holder)
                    }

                })
                animator.start()
            } else if(!preInfo.isExpanded){
                val animator = ObjectAnimator.ofFloat(holder.binding.ivArrow, View.ROTATION, 0f)
               animator.addListener(object :AnimatorListenerAdapter() {
                   override fun onAnimationEnd(animation: Animator?) {
                       dispatchAnimationFinished(holder)
                   }
               })
                animator.start()
            }
            return true
        }
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        if (changeFlags == RecyclerView.ItemAnimator.FLAG_CHANGED) {
            for (payload in payloads) {
                if (payload as? Int == ARROW_ANIM_UP) {
                    return TripItemHolderInfo(true)
                } else if(payload as Int == ARROW_ANIM_DOWN) {
                    return TripItemHolderInfo(false)
                }
            }
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    class TripItemHolderInfo(val isExpanded: Boolean) : ItemHolderInfo()


}

