<?php

namespace App\Traits;

use Illuminate\Http\Request;

trait PatientTrait
{
    public function saveRequestData(Request $request, $patient): void
    {
        $patient->name = ($request->name != null && $request->name != "")?$request->name:$patient->name;
        $patient->room_photo_path = ($request->room_photo_path != null)?$request->room_photo_path:$patient->room_photo_path;
        $patient->is_stopped = ($request->is_stopped != null)?$request->is_stopped:$patient->is_stopped;
        $patient->save();
    }
}
