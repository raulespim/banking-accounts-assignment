package com.raulespim.bankingaccounts.core.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raulespim.bankingaccounts.features.accounts.data.local.AccountDao
import com.raulespim.bankingaccounts.features.accounts.data.local.AccountEntity

@Database(
    entities = [AccountEntity::class],
    version = 1
)
abstract class BankingAccountsDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
}