<?php

namespace App\Traits;

use Illuminate\Contracts\Foundation\Application;
use Illuminate\Contracts\Routing\ResponseFactory;
use Illuminate\Http\Response;

trait ApiResponseTrait
{
    public function apiResponse($data=null,$status=null,$message=null): Response|Application|ResponseFactory
    {
        $array=[
//            'key'=>$data,
//            'status'=>$status,
//            'message'=>$message,
        ];
        return response($data,$status);
    }
}
