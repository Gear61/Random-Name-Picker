package com.randomappsinc.studentpicker.backupandrestore

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.randomappsinc.studentpicker.backupandrestore.backup.BackupDataFragment
import com.randomappsinc.studentpicker.backupandrestore.restore.RestoreDataFragment

class BackupAndRestoreTabsAdapter(
        fragmentManager: FragmentManager,
        private val tabNames: Array<String>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BackupDataFragment()
            1 -> RestoreDataFragment()
            else -> throw IllegalArgumentException("There should only be 2 tabs!")
        }
    }

    override fun getCount(): Int {
        return tabNames.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabNames[position]
    }

}
