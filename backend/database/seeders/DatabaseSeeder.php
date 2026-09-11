<?php

namespace Database\Seeders;

// use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // \App\Models\User::factory(10)->create();
//        \App\Models\Nurse::factory(10)->create();
//        \App\Models\CaringType::factory(10)->create();
//        \App\Models\Patient::factory(10)->create();
//        \App\Models\Caring::factory(10)->create();


        // here we specify our seeders classes to seed the db with our real data in single command
        $this->call([
            NurseSeeder::class,
            CaringtypeSeeder::class,
            PatientSeeder::class,
            CaringSeeder::class,
        ]);

        // \App\Models\User::factory()->create([
        //     'name' => 'Test User',
        //     'email' => 'test@example.com',
        // ]);
    }
}
