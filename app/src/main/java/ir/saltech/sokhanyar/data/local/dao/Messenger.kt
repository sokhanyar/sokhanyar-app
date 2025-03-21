package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.saltech.sokhanyar.data.local.entity.Chat
import ir.saltech.sokhanyar.data.local.entity.Media
import ir.saltech.sokhanyar.data.local.entity.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media WHERE uploaderId = :uploaderId")
    fun getByUploaderId(uploaderId: String): List<Media>

    @Query("SELECT * FROM media WHERE checksum = :checksum")
    fun findByChecksum(checksum: String): Media?

    @Query("SELECT * FROM media WHERE id = :mediaId")
    fun findById(mediaId: String): Media?

    @Insert
    fun add(media: Media)

    @Delete
    fun remove(media: Media)

    @Update
    fun update(media: Media)
}

@Dao 
interface ChatDao {
    @Query("SELECT * FROM chat")
    fun getAll(): List<Chat>
    
    @Query("SELECT * FROM chat WHERE id = :chatId")
    fun findById(chatId: String): Chat?
    
    @Query("SELECT * FROM message WHERE chatId = :chatId ORDER BY sentAt DESC")
    fun getChatMessages(chatId: String): Flow<List<Message>>
    
    @Insert
    fun add(chat: Chat)
    
    @Delete
    fun remove(chat: Chat)
    
    @Update
    fun update(chat: Chat)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM message WHERE chatId = :chatId")
    fun getByChatId(chatId: String): List<Message>
    
    @Query("SELECT * FROM message WHERE id = :messageId") 
    fun findById(messageId: String): Message?
    
    @Insert
    fun add(message: Message)
    
    @Delete
    fun remove(message: Message)
    
    @Update
    fun update(message: Message)
}