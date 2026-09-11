<?php

namespace App\Traits;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use function PHPUnit\Framework\isEmpty;

trait NurseTrait
{
    public function saveRequestData(Request $request, $nurse): void
    {
        $nurse->name = ($request->name != null && $request->name !="")?$request->name:$nurse->name;
        $nurse->gender = ($request->gender != null)?$request->gender:$nurse->gender;
        $nurse->phone = ($request->phone != null && $request->phone !="")?$request->phone:$nurse->phone;
        $nurse->is_resigned = ($request->is_resigned != null)?$request->is_resigned:$nurse->is_resigned;
        $nurse->is_admin = ($request->is_admin != null)?$request->is_admin:$nurse->is_admin;
        $nurse->email = ($request->email != null && $request->email != "")?$request->email:$nurse->email;
        $nurse->password= ($request->password != null && $request->password != "")?Hash::make($request->password):$nurse->password;
        $nurse->fcm_token = ($request->fcm_token != null && $request->fcm_token != "")?$request->fcm_token:$nurse->fcm_token;
        $nurse->save();
    }
}
