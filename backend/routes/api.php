<?php

use App\Http\Controllers\Api\CaringController;
use App\Http\Controllers\Api\CaringtypeController;
use App\Http\Controllers\Api\NurseController;
use App\Http\Controllers\Api\PatientController;
use App\Http\Controllers\AuthController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/nurse', function (Request $request) {
    return $request->nurse();
});

Route::controller(AuthController::class)->middleware('api')->prefix('auth')
    ->group(function () {
    Route::post('/login', 'login');
    Route::post('/register', 'register');
    Route::post('/logout', 'logout');
    Route::post('/refresh', 'refresh');
    Route::get('/nurseProfile', 'nurseProfile');
});

// protected
Route::group(['middleware' => ['jwt.verify']], function() {
    Route::controller(NurseController::class)->middleware('CheckAdmin')
        ->group(function (){
        Route::get('/nurses','getNurses');
        Route::get('/nurses/{id}','getNurse');
        Route::post('/nurses','createNurse');
        Route::patch('/nurses','updateNurse');
        Route::delete('/nurses/{id}','deleteNurse');
    });

    Route::controller(CaringtypeController::class)->middleware('CheckAdmin')
        ->group(function (){
        Route::get('/caringtypes','getCaringTypes');
        Route::get('/caringtypes/{id}','getCaringType');
        Route::post('/caringtypes','createCaringType');
        Route::patch('/caringtypes','updateCaringType');
        Route::delete('/caringtypes/{id}','deleteCaringType');
    });

    Route::controller(PatientController::class)->middleware('CheckAdmin')
        ->group(function (){
        Route::get('/patients','getPatients');
        Route::get('/patients/{id}','getPatient');
        Route::post('/patients','createPatient');
        Route::patch('/patients','updatePatient');
        Route::delete('/patients/{id}','deletePatient');
    });

    Route::get('/carings/nurse/{nurse_id}',[CaringController::class,'getByNurseId']);
    Route::get('/carings/check/{id}',[CaringController::class,'checkCaring']);
    Route::controller(CaringController::class)->middleware('CheckAdmin')
        ->group(function (){
        Route::get('/carings','getCarings');
        Route::get('/carings/trashed','getTrashed');
        Route::get('/carings/trashed/{id}','restoreTrashed');
        Route::patch('/carings/trashed','updateTrashed');
        Route::delete('/carings/prune/{id}','pruneCaring');
        Route::get('/carings/{id}','getCaring');
        Route::get('/carings/caringtype/{caringtype_id}','getByCaringTypeId');
        Route::get('/carings/patient/{patient_id}','getByPatientId');
        Route::post('/carings','createCaring');
        Route::patch('/carings','updateCaring');
        Route::delete('/carings/{id}','deleteCaring');

    });
});


