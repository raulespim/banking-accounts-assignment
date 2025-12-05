package com.raulespim.bankingaccounts.features.accounts.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.raulespim.bankingaccounts.features.accounts.data.local.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Upsert
    suspend fun upsertAll(accounts: List<AccountEntity>)

    @Query("SELECT * FROM AccountEntity ORDER BY isFavorite DESC, accountNickname ASC")
    fun getAllAccountsOrdered(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM AccountEntity WHERE isFavorite = 1 LIMIT 1")
    fun getFavoriteAccount(): Flow<AccountEntity?>

    @Query("UPDATE AccountEntity SET isFavorite = 0")
    suspend fun clearFavorite()

    @Transaction
    suspend fun setFavorite(accountId: String) {
        clearFavorite()
        updateFavoriteFlag(accountId, true)
    }

    @Query("UPDATE AccountEntity SET isFavorite = :isFavorite WHERE id = :accountId")
    suspend fun updateFavoriteFlag(accountId: String, isFavorite: Boolean)

    @Query("DELETE FROM AccountEntity")
    suspend fun clearAll()
}