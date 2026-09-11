<?php

use App\Http\Controllers\CaringController;
use App\Http\Controllers\CaringtypeController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\NurseController;
use App\Http\Controllers\PatientController;
use App\Http\Controllers\ProfileController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/


/*-----------------------------------------------splade-Breeze Route-----------------------------------------------*/
Route::middleware('splade')->group(function () {
    // Registers routes to support the interactive components...
    Route::spladeWithVueBridge();

    // Registers routes to support password confirmation in Form and Link components...
    Route::spladePasswordConfirmation();

    // Registers routes to support Table Bulk Actions and Exports...
    Route::spladeTable();

    // Registers routes to support async File Uploads with Filepond...
    Route::spladeUploads();

    Route::get('/', function () {
        return view('auth/login');
    });

    Route::get('/api-docs',[DashboardController::class,'redirectToApiDocs'])->name('enurse.apidocs');

    Route::middleware('auth')->group(function () {
        Route::get('/dashboard',[DashboardController::class,'index'])->middleware(['verified'])->name('dashboard');
        Route::get('/dashboard/export', [DashboardController::class, 'export'])->middleware(['verified'])->name('dashboard.export');
        Route::get('/profile', [ProfileController::class, 'edit'])->name('profile.edit');
        Route::patch('/profile', [ProfileController::class, 'update'])->name('profile.update');
        Route::delete('/profile', [ProfileController::class, 'destroy'])->name('profile.destroy');

        /*-----------------------------------------------Nurse Route-----------------------------------------------*/
        Route::patch('nurses/fcmtoken', [NurseController::class, 'updateToken'])->name('nurses.fcmtoken');
        Route::resource('nurses',NurseController::class)->middleware('CheckAdmin');

        /*-----------------------------------------------Caring Route-----------------------------------------------*/
        Route::patch('carings/check/{id}', [CaringController::class, 'check'])->name('carings.check');
        Route::controller(CaringController::class)->middleware('CheckAdmin')->group(function (){
            Route::delete('carings/{id}/prune','forceDelete')->name('carings.prune');
            Route::patch('carings/{id}/update', 'updateTrashed')->name('carings.update-trashed');
            Route::get('carings/{id}/edit', 'editTrashed')->name('carings.edit-trashed');
            Route::patch('carings/{id}/restore','restore')->name('carings.restore');
            Route::get('carings/trashed', 'showTrashed')->name('carings.trashed');
        });

        Route::resource('carings',CaringController::class)->middleware('CheckAdmin');

        /*-----------------------------------------------Caringtype Route-----------------------------------------------*/
        Route::resource('caringtypes',CaringtypeController::class)->middleware('CheckAdmin');

        /*-----------------------------------------------Patient Route-----------------------------------------------*/
        Route::resource('patients',PatientController::class)->middleware('CheckAdmin');
    });

    require __DIR__.'/auth.php';

});
