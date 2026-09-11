<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\NurseStoreRequest;
use App\Http\Requests\ProfileUpdateRequest;
use App\Http\Resources\CaringResource;
use App\Http\Resources\NurseResource;
use App\Models\Nurse;
use App\Traits\ApiResponseTrait;
use App\Traits\NurseTrait;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class NurseController extends Controller
{
    use NurseTrait, ApiResponseTrait;

    /**
     * @OA\Get(
     *     path="/api/nurses",
     *     tags={"Nurse"},
     *     summary="Get All Nurses",
     *     description="Multiple status values can be provided with comma separated string",
     *     operationId="getNurses",
     *     deprecated=false,
     *     @OA\Response(response=200,description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Nurse")
     *      )
     *    ),
     *  @OA\Response(response=401,description="Unauthorized"),
     *   security={{ "apiAuth": {} }}
     * )
     */
   public function getNurses()
    {
        $nurses = NurseResource::collection(Nurse::get());
        if($nurses->isEmpty()){
            return $this->apiResponse(null,404,'No Nurses was found');
        }
        return $this->apiResponse($nurses,200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/nurses/{id}",
     *     tags={"Nurse"},
     *     summary="Get Nurse",
     *     description="Nurse id should be provided",
     *     operationId="getNurse",
     *     @OA\Parameter(
     *       name="id",
     *       in="path",
     *       description="Get Nurse",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Nurse")
     *       )
     *     ),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getNurse($id)
    {
        $nurse = Nurse::find($id);
        if(!$nurse){
            return $this->apiResponse(null,404,'A Nurse with specified id not found');
        }
        return $this->apiResponse(new NurseResource($nurse),200,'ok');
    }

    /**
     * @OA\Post(
     *   path="/api/nurses",
     *   tags={"Nurse"},
     *   summary="Create Nurse",
     *   description="Multiple values can be provided with comma separated string",
     *   operationId="createNurse",
     *   @OA\RequestBody(
     *     description="Nurse to create",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Nurse")
     *   ),
     *  @OA\Response(response="201",description="Nurse created",
     *   @OA\JsonContent(
     *     @OA\Property(
     *      title="data",
     *      property="data",
     *      type="object",
     *      ref="#/components/schemas/Nurse"
     *     ),
     *    ),
     *  ),
     *  @OA\Response(response=400,description="Bad Request"),
     *  @OA\Response(response=422,description="Validation exception"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function createNurse(NurseStoreRequest $request)
    {
        $nurse = new Nurse();
        $this->saveRequestData($request, $nurse);
        return $this->apiResponse(new NurseResource($nurse),201,'ok');
    }

    /**
     * @OA\Patch(
     *   path="/api/nurses",
     *   tags={"Nurse"},
     *   summary="Update Nurse",
     *   description="Update Nurse",
     *   operationId="updateNurse",
     *   @OA\Response(response="201",description="Nurse updated",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="data",
     *       property="data",
     *       type="object",
     *       ref="#/components/schemas/Nurse"
     *       ),
     *     ),
     *   ),
     *   @OA\Response(response="400",description="Bad request."),
     *   @OA\Response(response="404",description="Nurse not found."),
     *   @OA\RequestBody(
     *     description="Nurse to update",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Nurse")
     *   ),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function updateNurse(Request $request)
    {
        $nurse = Nurse::where('email', $request->email)->first();
        if(!$nurse){
            return $this->apiResponse(null,404,'A Nurse with specified email not found');
        }
        $this->saveRequestData($request, $nurse);
        return $this->apiResponse(new NurseResource($nurse),200,'ok');
    }

    /**
     * @OA\Delete(
     *  path="/api/nurses/{id}",
     *  summary="Delete a Nurse",
     *  description="Delete Nurse",
     *  operationId="deleteNurse",
     *  tags={"Nurse"},
     *  @OA\Parameter(ref="#/components/parameters/Nurse--id"),
     *  @OA\Response(response=200,description="ok"),
     *  @OA\Response(response=401,description="Unauthorized"),
     *  @OA\Response(response=404,description="Nurse not found"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function deleteNurse($id)
    {
        $nurse = Nurse::find($id);
        if(!$nurse){
            return $this->apiResponse(null,404,'A Nurse with specified id not found');
        }
        $nurse->delete();
        return $this->apiResponse(new NurseResource($nurse), 200,'ok');
    }
}
