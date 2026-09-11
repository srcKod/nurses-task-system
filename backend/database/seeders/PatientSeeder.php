<?php

namespace Database\Seeders;

use App\Models\Patient;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class PatientSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::statement('SET FOREIGN_KEY_CHECKS = 0');
        Patient::truncate();
        DB::statement('SET FOREIGN_KEY_CHECKS = 1');

        Patient::create([
            'name'=>'Patient 1',
            'room_photo_path'=>'/img/room1.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 2',
            'room_photo_path'=>'/img/room2.jpg',
            'is_stopped'=>1
        ]);
        Patient::create([
            'name'=>'Patient 3',
            'room_photo_path'=>'/img/room3.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 4',
            'room_photo_path'=>'/img/room4.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 5',
            'room_photo_path'=>'/img/room5.jpg',
            'is_stopped'=>1
        ]);
        Patient::create([
            'name'=>'Patient 6',
            'room_photo_path'=>'/img/room6.jpg',
            'is_stopped'=>1
        ]);
        Patient::create([
            'name'=>'Patient 7',
            'room_photo_path'=>'/img/room7.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 8',
            'room_photo_path'=>'/img/room8.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 9',
            'room_photo_path'=>'/img/room9.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 10',
            'room_photo_path'=>'/img/room10.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 11',
            'room_photo_path'=>'/img/room11.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 12',
            'room_photo_path'=>'/img/room12.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 13',
            'room_photo_path'=>'/img/room13.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 14',
            'room_photo_path'=>'/img/room14.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 15',
            'room_photo_path'=>'/img/room15.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 16',
            'room_photo_path'=>'/img/room16.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 17',
            'room_photo_path'=>'/img/room17.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 18',
            'room_photo_path'=>'/img/room18.jpg',
            'is_stopped'=>0
        ]);
        Patient::create([
            'name'=>'Patient 19',
            'room_photo_path'=>'/img/room19.jpg',
            'is_stopped'=>0
        ]);
    }
}
