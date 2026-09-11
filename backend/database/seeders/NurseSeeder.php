<?php

namespace Database\Seeders;

use App\Models\Nurse;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;

class NurseSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::statement('SET FOREIGN_KEY_CHECKS = 0');
        Nurse::truncate();
        DB::statement('SET FOREIGN_KEY_CHECKS = 1');

        Nurse::create([
            'name'=>'Admin',
            'gender'=>1,
            'phone'=>'0013120000',
            'is_resigned'=>0,
            'is_admin'=>1,
            'email'=>'admin@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 1',
            'gender'=>1,
            'phone'=>'0013120001',
            'is_resigned'=>0,
            'is_admin'=>1,
            'email'=>'nurse1@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 2',
            'gender'=>0,
            'phone'=>'0013120002',
            'is_resigned'=>0,
            'is_admin'=>1,
            'email'=>'nurse2@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse Charlie',
            'gender'=>0,
            'phone'=>'0013120003',
            'is_resigned'=>0,
            'is_admin'=>0,
            'email'=>'nurse3@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 3',
            'gender'=>1,
            'phone'=>'0013120004',
            'is_resigned'=>0,
            'is_admin'=>0,
            'email'=>'nurse4@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 4',
            'gender'=>1,
            'phone'=>'0013120005',
            'is_resigned'=>0,
            'is_admin'=>0,
            'email'=>'nurse5@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 5',
            'gender'=>0,
            'phone'=>'0013120006',
            'is_resigned'=>0,
            'is_admin'=>0,
            'email'=>'nurse6@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 6',
            'gender'=>0,
            'phone'=>'0013120007',
            'is_resigned'=>0,
            'is_admin'=>0,
            'email'=>'nurse7@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 7',
            'gender'=>0,
            'phone'=>'0013120008',
            'is_resigned'=>0,
            'is_admin'=>0,
            'email'=>'nurse8@example.com',
            'password'=>Hash::make('123456789')
        ]);
        Nurse::create([
            'name'=>'Nurse 8',
            'gender'=>1,
            'phone'=>'0013120009',
            'is_resigned'=>0,
            'is_admin'=>0,
            'email'=>'nurse9@example.com',
            'password'=>Hash::make('123456789')
        ]);
    }
}
