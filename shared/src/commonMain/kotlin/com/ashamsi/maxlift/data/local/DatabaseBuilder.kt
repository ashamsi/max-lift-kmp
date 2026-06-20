package com.ashamsi.maxlift.data.local

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(ctx: Any?): RoomDatabase.Builder<AppDatabase>
